package frl.hacklab.hw3.repositories;

import frl.hacklab.hw3.config.Hw3Properties;
import frl.hacklab.hw3.dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;

@Repository
public class PlayerRepository
    extends JsonRepository<Player>
    implements CrudInterface<Player>
{
    public PlayerRepository(@Autowired Hw3Properties prop)
    {
        super(prop, "players");
        super.load(Player.class);
    }

    @Override
    public RepositoryResult create(Player player)
    {
        if(player.hasAllFields("id"))
        {
            player.setID(super.nextId());
            super.values.add(player);
            super.save();

            return RepositoryResult.Created;
        }

        //TODO: log
        return RepositoryResult.CreatePartial;
    }

    @Override
    public Optional<Player> read(Player player)
    {
        return super.values.stream()
            .filter(x -> x.getID() == player.getID())
            .findFirst();
    }

    @Override
    public RepositoryResult update(Player player, boolean onlyAllowFullUpdates)
    {
        if(onlyAllowFullUpdates && player.hasEmptyFields())
        {
            //TODO: Logger
            System.out.println("Tried partially updating a player when its explicitly not allowed to do so.");
            return RepositoryResult.UpdatePartialNotAllowed;
        }

        Optional<Player> foundPlayerOpt = read(player);

        if(foundPlayerOpt.isEmpty())
        {
            //TODO Logger;
            System.out.println("Tried updating a player that doesn't exist.");
            return RepositoryResult.NotFound;
        }

        RepositoryResult deletionResult = delete(player);
        if(deletionResult == RepositoryResult.Deleted)
        {
            RepositoryResult creationResult = create(player);
            if(creationResult == RepositoryResult.Created)
            {
                return RepositoryResult.Updated;
            }
            else
            {
                //TODO: log
                return creationResult;
            }
        }
        else
        {
            //TODO: log
            return deletionResult;
        }
    }

    @Override
    public RepositoryResult delete(Player player)
    {
        Optional<Player> foundPlayer = read(player);

        if(foundPlayer.isPresent())
        {
            boolean removed = values.removeIf(iterator -> iterator.getID() == foundPlayer.get().getID());

            if(removed)
            {
                super.save();
            }
            else
            {
                //TODO: log
                return RepositoryResult.NotFound;
            }

            //TODO: log
            return RepositoryResult.Deleted;
        }

        //TODO: log
        return RepositoryResult.NotFound;
    }

    @Override
    public HashSet<Player> everything()
    {
        return new HashSet<>(super.values);
    }
}
