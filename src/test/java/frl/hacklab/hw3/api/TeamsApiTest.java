package frl.hacklab.hw3.api;

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
        HashSet<Team> mockedTeams = new HashSet<>();
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
        Team expectedTeam = new Team(9);

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
    @DisplayName("getTeam fetches an non-existent team")
    void getTeam_does_not_exist()
    {
        //-- Arrange
        Team nonExistingTeam = new Team();
        nonExistingTeam.setID(19);

        when(teamsRepository.read(nonExistingTeam))
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

    @Test
    @DisplayName("addTeam gracefully handle duplicates")
    void addTeam_duplicate()
    {
        //-- Arrange
        Team teamDuplicate = new Team();
        teamDuplicate.setID(12);
        teamDuplicate.setName("FC knudde");

        teamsRepository.create(teamDuplicate);

        when(teamsRepository.create(teamDuplicate))
                .thenReturn(RepositoryResult.Created);

        //-- Act
        ResponseEntity<GenericApiResponse> result = this.teamsApi.addTeam(teamDuplicate);

        // -- Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message).isEqualTo("OK");
    }

    @Test
    @DisplayName("addTeam deals with team null")
    void addTeam_nulls()
    {
        //-- Arrange
        Team teamAdd = null;

        when(teamsRepository.create(teamAdd))
                .thenReturn(RepositoryResult.CreatePartial);

        //-- Act
        ResponseEntity<GenericApiResponse> result = this.teamsApi.addTeam(teamAdd);

        // -- Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message).contains("Error: " + RepositoryResult.NullValue);
    }

    @Test
    @DisplayName("overwriteTeam overwrites a team")
    void overwriteTeam_overwrite()
    {
        //-- Arrange
        Team teamToOverwrite = new Team(0, "TeamA", "CityA", "StadiumA");
        Team teamOverwritten = new Team(0, "TeamC", "CityC", "StadiumC");

        //TODO: This is wrong, it should use stubbing.
        teamsRepository.create(teamToOverwrite);
        teamsRepository.create(
            new Team(1, "TeamB", "CityB", "StadiumB")
        );

        when(teamsRepository.update(teamOverwritten, true))
                .thenReturn(RepositoryResult.Updated);

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.overwriteTeam(
            teamOverwritten
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("overwriteTeam overwrite a team partially.")
    void overwriteTeam_overwrite_partially()
    {
        //-- Arrange
        Team teamOverwrite = new Team();
        teamOverwrite.setID(12);
        teamOverwrite.setName("");

        teamsRepository.create(
            new Team(12, "TeamB", "CityB", "StadiumB")
        );

        when(teamsRepository.update(teamOverwrite, true))
                .thenReturn(RepositoryResult.UpdatePartialNotAllowed);

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.overwriteTeam(
            teamOverwrite
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("Error");
    }

    @Test
    @DisplayName("overwriteTeam overwrite by null")
    void overwriteTeam_overwrite_null()
    {
        //-- Arrange
        Team teamOverwrite = null;

        teamsRepository.create(
            new Team(12, "TeamB", "CityB", "StadiumB")
        );

        when(teamsRepository.update(teamOverwrite, true))
                .thenReturn(RepositoryResult.UpdatePartialNotAllowed);

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.overwriteTeam(
            teamOverwrite
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("NullValue");
    }

    @Test
    @DisplayName("overwriteTeam overwrite a non-existent team.")
    void overwriteTeam_overwrite_non_existent()
    {
        //-- Arrange
        Team teamNonExistent = new Team();
        teamNonExistent.setID(1234);
        teamNonExistent.setName("");

        teamsRepository.create(
            new Team(0, "TeamA", "CityA", "StadiumA")
        );

        when(teamsRepository.update(teamNonExistent, true))
                .thenReturn(RepositoryResult.UpdatePartialNotAllowed);

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.overwriteTeam(
            teamNonExistent
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("Error");
    }

    @Test
    @DisplayName("patchTeam patch a field.")
    void patchTeam_patch()
    {
        //-- Arrange
        Team teamToPatch = new Team(1234, "TeamA", "CityA", "StadiumA");

        Team theActualPatch = new Team(1234);
        theActualPatch.setCity("CityABC");

        teamsRepository.create(teamToPatch);

        when(teamsRepository.read(theActualPatch))
                .thenReturn(Optional.of(teamToPatch));

        when(teamsRepository.update(teamToPatch, false))
                .thenReturn(RepositoryResult.Updated);

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.patchTeam(
            theActualPatch
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).isEqualTo("OK");
    }

    @Test
    @DisplayName("patchTeam patch a field of a non-existent team")
    void patchTeam_patch_non_existent_team()
    {
        //-- Arrange
        Team teamToPatch = new Team(1, "TeamA", "CityA", "StadiumA");

        Team theActualPatch = new Team(1234);
        theActualPatch.setCity("CityABC");

        teamsRepository.create(teamToPatch);

        when(teamsRepository.read(theActualPatch))
                .thenReturn(Optional.empty());

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.patchTeam(
            theActualPatch
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("patchTeam patch a field of a null team")
    void patchTeam_patch_null_team()
    {
        //-- Arrange
        Team teamToPatch = new Team(1234, "TeamA", "CityA", "StadiumA");

        Team theActualPatch = null;

        teamsRepository.create(teamToPatch);

        when(teamsRepository.read(null))
                .thenReturn(Optional.empty());

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.patchTeam(
            theActualPatch
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("patchTeam patch a field but update failed")
    void patchTeam_patch_no_update()
    {
        //-- Arrange
        Team teamToPatch = new Team(1234, "TeamA", "CityA", "StadiumA");

        Team theActualPatch = new Team(1234);
        theActualPatch.setCity("CityABC");

        teamsRepository.create(teamToPatch);

        when(teamsRepository.read(theActualPatch))
                .thenReturn(Optional.of(teamToPatch));

        when(teamsRepository.update(teamToPatch, false))
                .thenReturn(RepositoryResult.NotFound);

        //--- Act
        ResponseEntity<GenericApiResponse> response = this.teamsApi.patchTeam(
            theActualPatch
        );

        //--- Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("Error");
    }

    @Test
    @DisplayName("deleteTeam delete a team.")
    void deleteTeam_delete()
    {
        Team teamToDelete = new Team(1234, "TeamA", "CityA", "StadiumA");

        when(teamsRepository.read(teamToDelete))
                .thenReturn(Optional.of(teamToDelete));

        when(teamsRepository.delete(teamToDelete))
                .thenReturn(RepositoryResult.Deleted);

        ResponseEntity<GenericApiResponse> response = this.teamsApi.deleteTeam(
            teamToDelete
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("OK");
    }

    @Test
    @DisplayName("deleteTeam delete a non-existent team")
    void deleteTeam_delete_non_existent()
    {
        Team teamToDelete = new Team(1234, "TeamA", "CityA", "StadiumA");

        when(teamsRepository.read(teamToDelete))
                .thenReturn(Optional.empty());

        ResponseEntity<GenericApiResponse> response = this.teamsApi.deleteTeam(
                teamToDelete
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("Error");
    }

    @Test
    @DisplayName("deleteTeam delete failed")
    void deleteTeam_delete_failed()
    {
        Team teamToDelete = new Team(1234, "TeamA", "CityA", "StadiumA");

        when(teamsRepository.read(teamToDelete))
                .thenReturn(Optional.of(teamToDelete));

        when(teamsRepository.delete(teamToDelete))
                .thenReturn(RepositoryResult.NotFound);

        ResponseEntity<GenericApiResponse> response = this.teamsApi.deleteTeam(
            teamToDelete
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("Error");
    }

    @Test
    @DisplayName("deleteTeam deletes null team")
    void deleteTeam_delete_null_team()
    {
        when(teamsRepository.read(null))
                .thenReturn(null);

        ResponseEntity<GenericApiResponse> response = this.teamsApi.deleteTeam(
                null
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message).contains("Error");
    }
}