package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_up_station"), nullable = false)
	private Station upStation;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_down_station"), nullable = false)
	private Station downStation;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_section_to_line"), nullable = false)
	private Line line;

	protected Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void toLine(Line line) {
		this.line = line;
		if (!line.getSections().contains(this)) {
			line.addSection(this);
		}
	}

	private int distance;

	public Long getId() {
		return id;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}

	public Line getLine() {
		return line;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation)
			&& Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, upStation, downStation, line);
	}

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", upStation=" + upStation +
			", downStation=" + downStation +
			", line=" + line +
			", distance=" + distance +
			'}';
	}
}
