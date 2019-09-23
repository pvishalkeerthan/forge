/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.game.replacement;

import forge.game.card.Card;
import forge.game.spellability.SpellAbility;

import java.util.Map;

/** 
 * TODO: Write javadoc for this type.
 *
 */
public class ReplaceGainLife extends ReplacementEffect {

    /**
     * Instantiates a new replace gain life.
     *
     * @param map the map
     * @param host the host
     */
    public ReplaceGainLife(Map<String, String> map, Card host, boolean intrinsic) {
        super(map, host, intrinsic);
    }

    /* (non-Javadoc)
     * @see forge.card.replacement.ReplacementEffect#canReplace(java.util.HashMap)
     */
    @Override
    public boolean canReplace(Map<String, Object> runParams) {
        if (((int)runParams.get("LifeGained")) <= 0) {
            return false;
        }
        if (hasParam("ValidPlayer")) {
            if (!matchesValid(runParams.get("Affected"), getParam("ValidPlayer").split(","), this.getHostCard())) {
                return false;
            }
        }
        if (hasParam("ValidSource")) {
            if (!matchesValid(runParams.get("Source"), getParam("ValidSource").split(","), this.getHostCard())) {
                return false;
            }
        }
        if ("True".equals(getParam("SourceController"))) {
            if (runParams.get("Source") == null || !runParams.get("Affected").equals(((Card)runParams.get("Source")).getController())) {
                return false;
            }
        }

        return true;
    }

    /* (non-Javadoc)
     * @see forge.card.replacement.ReplacementEffect#setReplacingObjects(java.util.HashMap, forge.card.spellability.SpellAbility)
     */
    @Override
    public void setReplacingObjects(Map<String, Object> runParams, SpellAbility sa) {
        sa.setReplacingObject("LifeGained", runParams.get("LifeGained"));
        sa.setReplacingObject("Player", runParams.get("Affected"));
    }

}
