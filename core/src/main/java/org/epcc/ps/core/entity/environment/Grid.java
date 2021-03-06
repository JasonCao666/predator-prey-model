/*
 * Copyright (C) 2017 the predator-prey-model authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.epcc.ps.core.entity.environment;

import org.epcc.ps.core.entity.creature.Species;

import java.io.Serializable;

/**
 *  <p>A class represents a single unit of landscape.</p>
 *  <p>Each grid contains a {@link Terrain} which implies the terrain of this grid.
 *  Grid also maintains densities for different {@link Species} and count of direct
 *  neighbors with land terrain.</p>
 *
 * @author shaohan.yin
 * @since 0.0.1
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
