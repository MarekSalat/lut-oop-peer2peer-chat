package fi.lut.oop.prj2.model.entities;

import fi.lut.oop.prj2.client.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* User: Marek Salát
* Date: 19.3.14
* Time: 16:03
*/
public class Group extends AbstractEntity<String>{
    public List<User> users = new ArrayList<User>();

    public Group() {
        setId(Utils.randomString(6));
    }

    public Group(String name){
        this.setId(name);
    }

    public void addUser(User user){
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public String[] getNames(){
        ArrayList<String> names = new ArrayList<String>();
        for(User user : users){
            names.add(user.getId());
        }

        return Arrays.copyOf(names.toArray(), names.size(), String[].class);
    }
}
