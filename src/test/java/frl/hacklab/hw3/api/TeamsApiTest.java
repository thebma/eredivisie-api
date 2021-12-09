package frl.hacklab.hw3.api;

import frl.hacklab.hw3.config.Hw3Properties;
import frl.hacklab.hw3.logging.ExtendedLogger;
import frl.hacklab.hw3.repositories.RepositoryResult;
import frl.hacklab.hw3.repositories.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import frl.hacklab.hw3.dto.Team;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class TeamsApiTest
{
    private TeamRepository teamsRepository;
    private TeamsApi teamsApi;

    @BeforeEach
    private void initApi()
    {
        this.teamsRepository = mock(TeamRepository.class);
        this.teamsApi = new TeamsApi(this.teamsRepository, new ExtendedLogger());
    }

    @Test
    @DisplayName("getAllTeams returns everything")
    void getAllTeams_happy_case()
    {
        //-- Arrange
        HashSet<Team> mockedTeams = new HashSet<Team>();
        mockedTeams.add(new Team());
        mockedTeams.add(new Team());

        when(teamsRepository.everything()).thenReturn(mockedTeams);

        //--- Act
        ResponseEntity<HashSet<Team>> response = this.teamsApi.getAllTeams();

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(mockedTeams.size());
    }

    @Test
    @DisplayName("getAllTeams gets empty result")
    void getAllTeams_gets_empty()
    {
        //-- Arrange
        HashSet<Team> mockedTeams = new HashSet<Team>();
        when(teamsRepository.everything()).thenReturn(mockedTeams);

        //--- Act
        ResponseEntity<HashSet<Team>> response = this.teamsApi.getAllTeams();

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(mockedTeams.size());
    }

    @Test
    @DisplayName("getTeam fetches a valid team.")
    void getTeam_happy_case()
    {
        //-- Arrange
        Team expectedTeam = new Team();
        expectedTeam.setID(9);

        when(teamsRepository.read(expectedTeam))
                .thenReturn(Optional.of(expectedTeam));

        //--- Act
        ResponseEntity<Team> response = this.teamsApi.getTeam(9);

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getID()).isEqualTo(expectedTeam.getID());
    }

    @Test
    @DisplayName("getTeam fetches an non-existant team")
    void getTeam_does_not_exist()
    {
        //-- Arrange
        Team notExistingTeam = new Team();
                notExistingTeam.setID(19);

        when(teamsRepository.read(notExistingTeam))
                .thenReturn(Optional.empty());

        //--- Act
        ResponseEntity<Team> response = this.teamsApi.getTeam(19);

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("addTeam adds a new team")
    void addTeam_happy_case()
    {
        //-- Arrange
        Team teamAdd = new Team(999, "A", "B", "C");

        when(teamsRepository.create(teamAdd))
                .thenReturn(RepositoryResult.Created);

        //-- Act
        ResponseEntity<GenericApiResponse> result = this.teamsApi.addTeam(teamAdd);

        // -- Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message).isEqualTo("OK");
    }

    @Test
    @DisplayName("addTeam adds partial team")
    void addTeam_adds_partial()
    {
        //-- Arrange
        Team teamAdd = new Team();
        teamAdd.setName("FC knudde");

        when(teamsRepository.create(teamAdd))
                .thenReturn(RepositoryResult.CreatePartial);

        //-- Act
        ResponseEntity<GenericApiResponse> result = this.teamsApi.addTeam(teamAdd);

        // -- Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message).contains("Error");
    }
}