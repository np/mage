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
package mage.cards.m;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.ControllerIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author LevelX2
 */
public class MoltenPrimordial extends CardImpl {

    public MoltenPrimordial(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{5}{R}{R}");
        this.subtype.add(SubType.AVATAR);

        this.power = new MageInt(6);
        this.toughness = new MageInt(4);

        // Haste
        this.addAbility(HasteAbility.getInstance());

        // When Molten Primordial enters the battlefield, for each opponent, take control of up to one target creature that player controls until end of turn. Untap those creatures. They have haste until end of turn.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new MoltenPrimordialEffect(),false));
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if (ability instanceof EntersBattlefieldTriggeredAbility) {
            ability.getTargets().clear();
            for(UUID opponentId : game.getOpponents(ability.getControllerId())) {
                Player opponent = game.getPlayer(opponentId);
                if (opponent != null) {
                    FilterCreaturePermanent filter = new FilterCreaturePermanent("creature from opponent " + opponent.getLogName());
                    filter.add(new ControllerIdPredicate(opponentId));
                    TargetCreaturePermanent target = new TargetCreaturePermanent(0,1, filter,false);
                    ability.addTarget(target);
                }
            }
        }
    }

    public MoltenPrimordial(final MoltenPrimordial card) {
        super(card);
    }

    @Override
    public MoltenPrimordial copy() {
        return new MoltenPrimordial(this);
    }
}

class MoltenPrimordialEffect extends OneShotEffect {

    public MoltenPrimordialEffect() {
        super(Outcome.GainControl);
        this.staticText = "for each opponent, take control of up to one target creature that player controls until end of turn. Untap those creatures. They have haste until end of turn";
    }

    public MoltenPrimordialEffect(final MoltenPrimordialEffect effect) {
        super(effect);
    }

    @Override
    public MoltenPrimordialEffect copy() {
        return new MoltenPrimordialEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean result = false;
        for (Target target: source.getTargets()) {
            if (target instanceof TargetCreaturePermanent) {
                Permanent targetCreature = game.getPermanent(target.getFirstTarget());
                if (targetCreature != null) {
                    ContinuousEffect effect1 = new GainControlTargetEffect(Duration.EndOfTurn);
                    effect1.setTargetPointer(new FixedTarget(targetCreature.getId()));
                    game.addEffect(effect1, source);

                    ContinuousEffect effect2 = new GainAbilityTargetEffect(HasteAbility.getInstance(), Duration.EndOfTurn);
                    effect2.setTargetPointer(new FixedTarget(targetCreature.getId()));
                    game.addEffect(effect2, source);

                    targetCreature.untap(game);
                    result = true;
                }
            }
        }
        return result;
    }
}

