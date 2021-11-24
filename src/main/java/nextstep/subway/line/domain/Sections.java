package nextstep.subway.line.domain;

import nextstep.subway.exception.*;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN_SECTIONS_SIZE = 2;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public void add(Section nonPersistSection) {
        if (!sections.isEmpty()) {
            validateSection(nonPersistSection);
            calculateBetweenStation(nonPersistSection);
        }

        sections.add(nonPersistSection);
    }

    private void calculateBetweenStation(Section nonPersistSection) {
        sections.stream()
                .filter(section -> section.isIncludeSection(nonPersistSection))
                .findAny()
                .ifPresent(section -> section.reArrangeAddSection(nonPersistSection));
    }

    private void validateSection(final Section nonPersistSection) {
        boolean isSameUpStation = getStations().isIn(nonPersistSection.getUpStation());
        boolean isSameDownStation = getStations().isIn(nonPersistSection.getDownStation());
        validateSameSectionStation(isSameUpStation, isSameDownStation);
        notIncludeOneStation(isSameUpStation, isSameDownStation);
    }

    private void notIncludeOneStation(final boolean isSameUpStation, final boolean isSameDownStation) {
        if (!isSameUpStation && !isSameDownStation) {
            throw new NotIncludeOneStationException();
        }
    }

    private void validateSameSectionStation(final boolean isSameUpStation, final boolean isSameDownStation) {
        if (isSameUpStation && isSameDownStation) {
            throw new SameSectionStationException();
        }
    }

    public Station findFirstUpStation() {
        Stations downStations = new Stations(findDownStations());
        return sections.stream().filter(
                section -> !downStations.isIn(section.getUpStation()))
                .findFirst()
                .orElseThrow(NotFoundStationException::new)
                .getUpStation();
    }

    private List<Station> findDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public Stations getStations() {
        Station findStation = findFirstUpStation();
        List<Station> sectionStationList = getSectionStationList(findStation);
        return new Stations(sectionStationList);
    }

    private List<Station> getSectionStationList(Station station) {
        List<Station> stations = new ArrayList<>();
        while (!Objects.isNull(station)) {
            stations.add(station);
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
        }
        return stations;
    }

    private Section findNextSection(final Station findStation) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(findStation))
                .findFirst()
                .orElse(new Section());
    }

    public void removeSectionByStation(Station deleteStation) {
        validateSectionsSize();
        List<Section> foundSections = findSectionsIncludeStation(deleteStation);
        validateIncludeSections(foundSections);
        Section deleteSection = foundSections.get(0);
        if (isNotTerminal(foundSections)) {
            Section updatedSection = foundSections.get(1);
            updatedSection.reArrangeDeleteSection(deleteSection, deleteStation);
        }

        sections.remove(deleteSection);
    }

    private void validateIncludeSections(List<Section> deleteSections) {
        if (deleteSections.isEmpty()) {
            throw new NotIncludeStation();
        }
    }

    private List<Section> findSectionsIncludeStation(Station deleteStation) {
        return sections.stream()
                .filter(section -> section.hasStation(deleteStation))
                .collect(Collectors.toList());
    }

    private void validateSectionsSize() {
        if (sections.size() < MIN_SECTIONS_SIZE) {
            throw new NotDeleteOneSectionException();
        }
    }

    private boolean isNotTerminal(List<Section> sections) {
        return sections.size() > 1;
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public int sumDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
