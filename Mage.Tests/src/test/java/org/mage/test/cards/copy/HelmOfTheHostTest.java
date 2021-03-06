/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package org.mage.test.cards.copy;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *
 * @author LevelX2
 */
public class HelmOfTheHostTest extends CardTestPlayerBase {

    /**
     * If you animate Gideon of the Trials and equip it with Helm of the Host
     * the nonlegendary copies can't become creatures with the 0 ability. You
     * can activate it just fine (and it gets put on the stack) but nothing
     * happens and you can't use another ability.
     */
    @Test
    public void testCopyPlaneswalker() {
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 5);
        //Starting Loyalty: 3
        // +1: Until your next turn, prevent all damage target permanent would deal.
        // 0: Until end of turn, Gideon of the Trials becomes a 4/4 Human Soldier creature with indestructible that's still a planeswalker. Prevent all damage that would be dealt to him this turn.
        // 0: You get an emblem with "As long as you control a Gideon planeswalker, you can't lose the game and your opponent can't win the game."
        addCard(Zone.BATTLEFIELD, playerA, "Gideon of the Trials", 1);
        // At the beginning of combat on your turn, create a token that’s a copy of equipped creature, except the token isn’t legendary if equipped creature is legendary. That token gains haste.
        // Equip {5}
        addCard(Zone.BATTLEFIELD, playerA, "Helm of the Host", 1);

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "0: Until end of turn");
        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Equip");

        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "0: Until end of turn");
        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "0: Until end of turn");

        attack(3, playerA, "Gideon of the Trials");
        attack(3, playerA, "Gideon of the Trials");

        setStopAt(4, PhaseStep.PRECOMBAT_MAIN);
        execute();

        assertPermanentCount(playerA, "Gideon of the Trials", 2);

        assertCounterCount("Gideon of the Trials", CounterType.LOYALTY, 3);

        assertLife(playerB, 12);
        assertLife(playerA, 20);
    }

}
