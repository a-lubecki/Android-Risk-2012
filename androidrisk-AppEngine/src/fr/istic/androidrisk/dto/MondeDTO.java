package fr.istic.androidrisk.dto;

import com.googlecode.objectify.annotation.Serialized;
import fr.istic.androidrisk.moteur.Region;
import fr.istic.androidrisk.moteur.Territoire;
import java.io.Serializable;
import java.util.HashMap;
import javax.persistence.Id;

public class MondeDTO implements Serializable {

    @Id private Long id = new Long(1);
    @Serialized private HashMap<String, Territoire> territoires = new HashMap<String, Territoire>();
    @Serialized private HashMap<Integer, Region> regions = new HashMap<Integer, Region>();

    public MondeDTO() {
    }

    public MondeDTO(HashMap<String, Territoire> territoires, HashMap<Integer, Region> regions) {
        this.territoires = territoires;
        this.regions = regions;
    }

    public HashMap<Integer, Region> getRegions() {
        return regions;
    }

    public void setRegions(HashMap<Integer, Region> regions) {
        this.regions = regions;
    }

    public HashMap<String, Territoire> getTerritoires() {
        return territoires;
    }

    public void setTerritoires(HashMap<String, Territoire> territoires) {
        this.territoires = territoires;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
