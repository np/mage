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
package mage.cards.o;

import mage.MageInt;
import mage.abilities.TriggeredAbility;
import mage.abilities.common.SpellCastOpponentTriggeredAbility;
import mage.abilities.condition.common.SourceMatchesFilterCondition;
import mage.abilities.decorator.ConditionalTriggeredAbility;
import mage.abilities.effects.common.continuous.BecomesCreatureSourceEffect;
import mage.abilities.keyword.FirstStrikeAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.filter.StaticFilters;
import mage.filter.common.FilterCreatureSpell;
import mage.game.permanent.token.TokenImpl;
import mage.game.permanent.token.Token;

import java.util.UUID;

/**
 *
 * @author LoneFox
 *
 */
public class OpalChampion extends CardImpl {

    public OpalChampion(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.ENCHANTMENT},"{2}{W}");

        // When an opponent casts a creature spell, if Opal Champion is an enchantment, Opal Champion becomes a 3/3 Knight creature with first strike.
        TriggeredAbility ability = new SpellCastOpponentTriggeredAbility(new BecomesCreatureSourceEffect(new OpalChampionKnight(), "", Duration.WhileOnBattlefield, true, false),
                new FilterCreatureSpell(), false);
        this.addAbility(new ConditionalTriggeredAbility(ability, new SourceMatchesFilterCondition(StaticFilters.FILTER_ENCHANTMENT_PERMANENT),
                "When an opponent casts a creature spell, if {this} is an enchantment, {this} becomes a 3/3 Knight creature with first strike."));
    }

    public OpalChampion(final OpalChampion card) {
        super(card);
    }

    @Override
    public OpalChampion copy() {
        return new OpalChampion(this);
    }
}

class OpalChampionKnight extends TokenImpl {

    public OpalChampionKnight() {
        super("Knight", "3/3 Knight creature with first strike");
        cardType.add(CardType.CREATURE);
        subtype.add(SubType.KNIGHT);
        power = new MageInt(3);
        toughness = new MageInt(3);
        this.addAbility(FirstStrikeAbility.getInstance());
    }
    public OpalChampionKnight(final OpalChampionKnight token) {
        super(token);
    }

    public OpalChampionKnight copy() {
        return new OpalChampionKnight(this);
    }
}
