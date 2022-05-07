package fr.istic.androidrisk.dto;

import fr.istic.androidrisk.moteur.Region;
import fr.istic.androidrisk.moteur.Territory;
import java.io.Serializable;
import java.util.HashMap;

public class MondeDTO implements Serializable {

    private Long id = new Long(1);
    private HashMap<String, Territory> territoires = new HashMap<String, Territory>();
    private HashMap<Integer, Region> regions = new HashMap<Integer, Region>();

    public MondeDTO() {
    }

    public MondeDTO(HashMap<String, Territory> territoires, HashMap<Integer, Region> regions) {
        this.territoires = territoires;
        this.regions = regions;
    }

    public HashMap<Integer, Region> getRegions() {
        return regions;
    }

    public void setRegions(HashMap<Integer, Region> regions) {
        this.regions = regions;
    }

    public HashMap<String, Territory> getTerritoires() {
        return territoires;
    }

    public void setTerritoires(HashMap<String, Territory> territoires) {
        this.territoires = territoires;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
