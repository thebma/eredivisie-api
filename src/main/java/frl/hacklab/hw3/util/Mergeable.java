package frl.hacklab.hw3.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Locale;

//TODO(bma, 08 dec 2021): Use logging for this class.
public class Mergeable<T>
{
    private T instantiate(Class<T> concreteType)
    {
        try
        {
            Constructor<T> constructorMethod = concreteType != null ? concreteType.getDeclaredConstructor() : null;

            if(constructorMethod != null)
            {
                return constructorMethod.newInstance();
            }
        }
        catch(NoSuchMethodException e)
        {
            System.out.println("Could not instantiate, constructor was not found.");
            return null;
        }
        catch(NullPointerException e)
        {
            System.out.println("Could not instantiate, constructor caused a null reference.");
            return null;
        }
        catch(IllegalAccessException e)
        {
            System.out.println("Could not instantiate, illegal to create new instance.");
            return null;

        }
        catch(InvocationTargetException e)
        {
            System.out.println("Could not instantiate, constructor invocation failed.");
            return null;
        }
        catch(InstantiationException e)
        {
            System.out.println("Could not instantiate, general instantiation failed.");
            return null;
        }

        return null;
    }

    public boolean hasEmptyFields(Class<T> concreteType, Object... except)
    {
        HashSet<String> exceptions = new HashSet<>();
        exceptions.add("concretetype");

        for (Object ex : except)
        {
            exceptions.add(ex.toString());
        }

        T defaultT = instantiate(concreteType);
        T selfT = (T)this;

        if(defaultT == null) return true;

        try
        {
            Field[] defaultFields = defaultT.getClass().getDeclaredFields();
            Field[] selfFields = concreteType != null ? concreteType.getDeclaredFields() : new Field[0];

            if(defaultFields.length == selfFields.length)
            {
                for(int i = 0; i < defaultFields.length; i++)
                {
                    Field selfField = selfFields[i];
                    Field defaultField = defaultFields[i];

                    //NOTE: only allow protected fields to be affected.
                    //      0x00000004 = value for protected as specified in Modifiers class.
                    var selfModifier = selfField.getModifiers();
                    if(selfModifier != 0x00000004) {
                        continue;
                    }

                    selfField.setAccessible(true);
                    defaultField.setAccessible(true);

                    if(exceptions.contains(defaultField.getName().toLowerCase(Locale.ROOT)))
                        continue;

                    Object defaultFieldValue = defaultFields[i].get(defaultT);
                    Object selfFieldValue = selfFields[i].get(selfT);

                    if(defaultFieldValue.equals(selfFieldValue))
                        return true;
                }

                return false;
            }
            else
            {
                //NOTE: Dunno if this behaviour is correct, this should never happen...
                //      Unless you're stupid enough to compare weakly typed like objects...
                return defaultFields.length > selfFields.length;
            }
        }
        catch(NullPointerException | IllegalAccessException e)
        {
            return true;
        }
    }

    public boolean hasAllFields(Class<T> concreteType, Object... except)
    {
        return !hasEmptyFields(concreteType, except);
    }

    public T merge(T other, Class<T> concreteType)
    {
        try
        {
            T defaultT = instantiate(concreteType);

            Field[] defaultFields = defaultT.getClass().getDeclaredFields();
            Field[] selfFields = concreteType != null ? concreteType.getDeclaredFields() : new Field[0];
            Field[] otherFields = other.getClass().getDeclaredFields();

            for(int i = 0; i < selfFields.length; i++)
            {
                Field selfField = selfFields[i];
                Field otherField = otherFields[i];
                Field defaultField = defaultFields[i];

                //NOTE: only allow protected fields to be affected.
                //      0x00000004 = value for protected as specified in Modifiers class.
                var selfModifier = selfField.getModifiers();
                if(selfModifier != 0x00000004) {
                    continue;
                }

                selfField.setAccessible(true);
                defaultField.setAccessible(true);
                otherField.setAccessible(true);

                Object otherFieldValue = otherField.get(other);
                Object defaultFieldValue = defaultField.get(defaultT);

                if(!otherFieldValue.equals(defaultFieldValue))
                {
                    selfField.set(this, otherFieldValue);
                }
            }
        }
        catch(IllegalAccessException | NullPointerException e)
        {
            System.out.println("Error when trying to merge." + e);
        }

        return (T)this;
    }
}
