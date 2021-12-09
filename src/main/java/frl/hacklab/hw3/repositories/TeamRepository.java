package frl.hacklab.hw3.repositories;

import frl.hacklab.hw3.config.Hw3Properties;
import frl.hacklab.hw3.dto.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TeamRepository
    extends JsonRepository<Team>
    implements CrudInterface<Team>
{
    public TeamRepository(@Autowired Hw3Properties prop)
    {
        super(prop,"teams");
        super.load(Team.class);
    }

    @Override
    public HashSet<Team> everything()
    {
        return new HashSet<>(super.values);
    }

    @Override
    public RepositoryResult create(Team team)
    {
        if(team.hasAllFields("id"))
        {
            team.setID(super.nextId());
            values.add(team);
            super.save();
            return RepositoryResult.Created;
        }

        //TODO: log
        return RepositoryResult.CreatePartial;
    }

    @Override
    public Optional<Team> read(Team team)
    {
        return super.values.stream()
            .filter(x -> x.getID() == team.getID())
            .findFirst();
    }

    @Override
    public RepositoryResult update(Team team, boolean onlyAllowFullUpdates)
    {
        if(onlyAllowFullUpdates && team.hasEmptyFields())
        {
            //Todo: logger
            System.out.println("Tried partially updating a team when its explicitly not allowed to do so.");
            return RepositoryResult.UpdatePartialNotAllowed;
        }

        if(team.getID() <= 0)
        {
            //TODO: logger
            System.out.println("Tried creating a new team with an update function.");
            return RepositoryResult.CreationNotAllowed;
        }

        RepositoryResult deletionResult = delete(team);
        if (deletionResult == RepositoryResult.Deleted)
        {
            RepositoryResult creationResult = create(team);
            if(creationResult == RepositoryResult.Created)
            {
                return RepositoryResult.Updated;
            }

            //TODO: log
            return creationResult;
        }

        //TODO: log
        return deletionResult;
    }

    @Override
    public RepositoryResult delete(Team team)
    {
        Optional<Team> foundTeam = this.read(team);

        if(foundTeam.isPresent())
        {
            boolean removed = values.removeIf(
                iterator -> iterator.getID() == foundTeam.get().getID()
            );

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
}