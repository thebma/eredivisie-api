package frl.hacklab.hw3.api;

import frl.hacklab.hw3.dto.Player;
import frl.hacklab.hw3.logging.ExtendedLogger;
import frl.hacklab.hw3.repositories.PlayerRepository;
import frl.hacklab.hw3.repositories.RepositoryResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@Repository
@RequestMapping("/players")
public class PlayerApi
{
    private final PlayerRepository playerRepo;
    private final ExtendedLogger logger;

    public PlayerApi(
            @NotNull @Autowired PlayerRepository playerRepo,
            @NotNull @Autowired ExtendedLogger logger
    )
    {
        this.playerRepo = playerRepo;

        this.logger = logger;
        this.logger.init(this.getClass());
    }


    @GetMapping({"", "/" })
    public ResponseEntity<HashSet<Player>> getAllPlayers()
    {
        //TODO(bma, 20 nov 2021): Some sort of pagination for the response.
        //     Responding with 4.5k lines is not particularly quick nor handy.
        HashSet<Player> players = playerRepo.everything();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable int id)
    {
        Player tempPlayer = new Player();
        tempPlayer.setID(id);

        Optional<Player> foundPlayerOpt = playerRepo.read(tempPlayer);

        if(foundPlayerOpt.isEmpty())
        {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Player foundPlayer = foundPlayerOpt.get();
        return new ResponseEntity<>(foundPlayer, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<GenericApiResponse> addPlayer(@RequestBody Player player)
    {
        RepositoryResult creationResult = playerRepo.create(player);

        if(creationResult == RepositoryResult.Created)
        {
            return ResponseEntity.ok(new GenericApiResponse());
        }

        logger.logResult(creationResult, "POST", "Player", "PlayersApi::addPlayer",
            player.getID(), player.getName()
        );

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + creationResult.toString()),
            HttpStatus.BAD_REQUEST
        );
    }

    @PutMapping("/")
    public ResponseEntity<GenericApiResponse> putPlayer(@RequestBody Player player)
    {
        RepositoryResult updateResult = playerRepo.update(player, true);

        if(updateResult == RepositoryResult.Updated)
        {
            return new ResponseEntity<>(
                new GenericApiResponse(),
                HttpStatus.OK
            );
        }

        logger.logResult(updateResult, "PUT", "Player", "PlayerApi::putPlayer",
                player.getID(), player.getName()
        );

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + updateResult.toString()),
            HttpStatus.BAD_REQUEST
        );
    }

    @PatchMapping("/")
    public ResponseEntity<GenericApiResponse> patchPlayer(@RequestBody Player player)
    {
        Optional<Player> foundPlayerOpt = playerRepo.read(player);

        if(foundPlayerOpt.isEmpty())
        {
            return new ResponseEntity<>(
                    new GenericApiResponse("Could not find a player to update."),
                    HttpStatus.BAD_REQUEST
            );
        }

        Player foundPlayer = foundPlayerOpt.get();
        foundPlayer = foundPlayer.merge(player, Player.class);

        RepositoryResult updateResult = playerRepo.update(foundPlayer, false);

        if(updateResult == RepositoryResult.Updated)
        {
            return ResponseEntity.ok(new GenericApiResponse());
        }

        logger.logResult(updateResult, "PATCH", "Player", "PlayersApi::patchPlayer");

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + updateResult),
            HttpStatus.BAD_REQUEST
        );
    }

    @DeleteMapping("/")
    public ResponseEntity<GenericApiResponse> deletePlayer(@RequestBody Player player)
    {
        RepositoryResult deletionResult = playerRepo.delete(player);

        if(deletionResult == RepositoryResult.Deleted)
        {
            return new ResponseEntity<>(
                new GenericApiResponse(),
                HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
            new GenericApiResponse("Error: " + deletionResult),
            HttpStatus.BAD_REQUEST
        );
    }
}
