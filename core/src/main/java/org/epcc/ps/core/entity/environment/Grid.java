package org.epcc.ps.core.entity.environment;

import org.epcc.ps.core.entity.creature.Species;

import java.io.Serializable;

/**
 * @author shaohan.yin
 * Created on 11/10/2017
 */
public class Grid implements Serializable {
    private Terrain terrain;
    private double[] densities;
    private int landNeighborCnt;

    public Grid(Terrain terrain) {
        this.terrain = terrain;
        densities = new double[Species.values().length];
        for (Species species : Species.values()) {
            densities[species.ordinal()] = 0.0;
        }
        this.landNeighborCnt = 0;
    }

    public Grid(Terrain terrain, double[] densities) {
        this.terrain = terrain;
        this.densities = densities;
        this.landNeighborCnt = 0;
    }

    public Grid(Terrain terrain, double[] densities, int landNeighborCnt) {
        this.terrain = terrain;
        this.densities = densities;
        this.landNeighborCnt = landNeighborCnt;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void updateDensity(Species species, double density) {
        densities[species.ordinal()] = density;
    }

    public double getDensity(Species species) {
        return densities[species.ordinal()];
    }

    public int getLandNeighborCnt() {
        return landNeighborCnt;
    }

    public void setLandNeighborCnt(int landNeighborCnt) {
        this.landNeighborCnt = landNeighborCnt;
    }

    @Override
    public String toString() {
        return String.format("Grid[%s]", terrain.toString());
    }

}
