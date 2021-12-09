package frl.hacklab.hw3.api;

import frl.hacklab.hw3.dto.Team;
import frl.hacklab.hw3.logging.ExtendedLogger;
import frl.hacklab.hw3.repositories.RepositoryResult;
import frl.hacklab.hw3.repositories.TeamRepository;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
public class TeamsApi
{
    private final TeamRepository teamRepo;
    private final ExtendedLogger logger;

    public TeamsApi(
        @NotNull @Autowired TeamRepository teamRepo,
        @NotNull @Autowired ExtendedLogger logger
    )
    {
        this.teamRepo = teamRepo;

        this.logger = logger;
        this.logger.init(TeamsApi.class);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<HashSet<Team>> getAllTeams()
    {
        HashSet<Team> teams = teamRepo.everything();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable int id)
    {
        Team tempTeam = new Team();
        tempTeam.setID(id);

        Optional<Team> team = teamRepo.read(tempTeam);

        if(team.isPresent())
        {
            return new ResponseEntity<>(team.get(), HttpStatus.OK);
        }
        else
        {
            logger.warn("Fetching team id #" + id + " could not be found.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/")
    public ResponseEntity<GenericApiResponse> addTeam(@RequestBody Team team)
    {
        RepositoryResult creationResult = teamRepo.create(team);

        if(creationResult == RepositoryResult.Created)
        {
            return new ResponseEntity<>(new GenericApiResponse(), HttpStatus.CREATED);
        }

        logger.logResult(creationResult, "POST", "Team", "TeamsApi::addTeam",
                team.getID() , team.getName()
        );

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + creationResult.toString()),
            HttpStatus.BAD_REQUEST
        );
    }

    @PutMapping("/")
    public ResponseEntity<GenericApiResponse> overwriteTeam(@RequestBody Team team)
    {
        RepositoryResult updateResult = teamRepo.update(team, true);

        if(updateResult == RepositoryResult.Updated)
        {
            return ResponseEntity.ok(new GenericApiResponse());
        }

        logger.logResult(updateResult, "PUT", "Team", "TeamsApi::overwriteTeam",
            team.getID(), team.getName()
        );

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + updateResult.toString()),
            HttpStatus.BAD_REQUEST
        );
    }

    @PatchMapping("/")
    public ResponseEntity<GenericApiResponse> patchTeam(@RequestBody Team team)
    {
        Optional<Team> foundTeamOpt = teamRepo.read(team);

        if(foundTeamOpt.isEmpty())
        {
            logger.error("Could not patch team, because team wasn't found with id: " + team.getID());

            return new ResponseEntity<>(
                new GenericApiResponse("Team could not be found to perform PATCH on."),
                HttpStatus.BAD_REQUEST
            );
        }

        Team foundTeam = foundTeamOpt.get();
        foundTeam.merge(team);

        RepositoryResult updateResult = teamRepo.update(foundTeam, false);

        if(updateResult == RepositoryResult.Updated)
        {
            return new ResponseEntity<>(
                new GenericApiResponse(),
                HttpStatus.CREATED
            );
        }

        switch(updateResult)
        {
            case NotFound:
                logger.error("Could not find a team id:{} name:{} at PATCH", team.getID(), team.getName());
                break;
            case CreationNotAllowed:
                logger.error("Could not create team via PATCH for team id:{} name{}", team.getID(), team.getName());
                break;
            case UpdatePartialNotAllowed:
                logger.error("Cannot partially update in PATCH for team id:{} name:{}", team.getID(), team.getName());
                break;
            case CreatePartial:
                logger.error("Cannot create a team for PATCH for team id:{} name:{}", team.getID(), team.getName());
                break;
        }

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + updateResult),
            HttpStatus.BAD_REQUEST
        );
    }

    @DeleteMapping("/")
    public ResponseEntity<GenericApiResponse> deleteTeam(@RequestBody Team team)
    {
        Optional<Team> optTeam = teamRepo.read(team);

        if(optTeam.isEmpty())
        {
            return new ResponseEntity<>(
                new GenericApiResponse("Team could not be found to perform DELETE on."),
                HttpStatus.BAD_REQUEST
            );
        }

        Team foundTeam = optTeam.get();
        RepositoryResult deletionResult = teamRepo.delete(foundTeam);

        if(deletionResult == RepositoryResult.Deleted)
        {
            return ResponseEntity.ok(new GenericApiResponse());
        }

        logger.logResult(deletionResult, "DELETE", "Team", "TeamsApi::deleteTeam",
                team.getID(), team.getName()
        );

        return new ResponseEntity<>(
            new GenericApiResponse("Team could not be deleted."),
            HttpStatus.BAD_REQUEST
        );
    }
}