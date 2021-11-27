package nextstep.subway.station.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class StationResponsesTest {
    @Test
    void 지하철_역_응답_리스트로_변환한다() {
        StationResponses stationResponses = StationResponses.from(
            Arrays.asList(new Station("강남역"), new Station("양재역")));

        assertThat(stationResponses).isEqualTo(new StationResponses(
            Arrays.asList(StationResponse.from(new Station("강남역")), StationResponse.from(new Station("양재역")))));
    }
}