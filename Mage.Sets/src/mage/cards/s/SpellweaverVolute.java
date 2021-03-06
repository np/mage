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
package mage.cards.s;

import java.util.UUID;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.filter.FilterSpell;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCardInGraveyard;

/**
 *
 * @author LevelX2
 */
public class SpellweaverVolute extends CardImpl {

    public SpellweaverVolute(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{U}{U}");

        this.subtype.add(SubType.AURA);

        // Enchant instant card in a graveyard
        FilterCard filter = new FilterCard("instant card in a graveyard");
        filter.add(new CardTypePredicate(CardType.INSTANT));
        TargetCardInGraveyard auraTarget = new TargetCardInGraveyard(filter);
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.Detriment));
        Ability ability = new EnchantAbility(auraTarget.getTargetName());
        this.addAbility(ability);

        // Whenever you cast a sorcery spell, copy the enchanted instant card. You may cast the copy without paying its mana cost.
        // If you do, exile the enchanted card and attach Spellweaver Volute to another instant card in a graveyard.
        FilterSpell filterSpell = new FilterSpell("a sorcery spell");
        filterSpell.add(new CardTypePredicate(CardType.SORCERY));
        this.addAbility(new SpellCastControllerTriggeredAbility(new SpellweaverVoluteEffect(), filterSpell, false));
    }

    public SpellweaverVolute(final SpellweaverVolute card) {
        super(card);
    }

    @Override
    public SpellweaverVolute copy() {
        return new SpellweaverVolute(this);
    }
}

class SpellweaverVoluteEffect extends OneShotEffect {

    public SpellweaverVoluteEffect() {
        super(Outcome.Benefit);
        this.staticText = "copy the enchanted instant card. You may cast the copy without paying its mana cost. \n"
                + "If you do, exile the enchanted card and attach {this} to another instant card in a graveyard";
    }

    public SpellweaverVoluteEffect(final SpellweaverVoluteEffect effect) {
        super(effect);
    }

    @Override
    public SpellweaverVoluteEffect copy() {
        return new SpellweaverVoluteEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Permanent sourcePermanent = game.getPermanent(source.getSourceId());
            if (sourcePermanent != null && sourcePermanent.getAttachedTo() != null) {
                Card enchantedCard = game.getCard(sourcePermanent.getAttachedTo());
                if (enchantedCard != null && game.getState().getZone(enchantedCard.getId()) == Zone.GRAVEYARD) {
                    Player ownerEnchanted = game.getPlayer(enchantedCard.getOwnerId());
                    if (ownerEnchanted != null
                            && controller.chooseUse(outcome, "Create a copy of " + enchantedCard.getName() + '?', source, game)) {
                        Card copiedCard = game.copyCard(enchantedCard, source, source.getControllerId());
                        if (copiedCard != null) {
                            ownerEnchanted.getGraveyard().add(copiedCard);
                            game.getState().setZone(copiedCard.getId(), Zone.GRAVEYARD);
                            if (controller.chooseUse(outcome, "Cast the copied card without paying mana cost?", source, game)) {
                                if (copiedCard.getSpellAbility() != null) {
                                    controller.cast(copiedCard.getSpellAbility(), game, true, new MageObjectReference(source.getSourceObject(game), game));
                                }
                                if (controller.moveCards(enchantedCard, Zone.EXILED, source, game)) {
                                    FilterCard filter = new FilterCard("instant card in a graveyard");
                                    filter.add(new CardTypePredicate(CardType.INSTANT));
                                    TargetCardInGraveyard auraTarget = new TargetCardInGraveyard(filter);
                                    if (auraTarget.canChoose(source.getSourceId(), controller.getId(), game)) {
                                        controller.choose(outcome, auraTarget, source.getSourceId(), game);
                                        Card newAuraTarget = game.getCard(auraTarget.getFirstTarget());
                                        if (newAuraTarget != null) {
                                            if (enchantedCard.getId().equals(newAuraTarget.getId())) {
                                            } else if (newAuraTarget.addAttachment(sourcePermanent.getId(), game)) {
                                                game.informPlayers(sourcePermanent.getLogName() + " was attached to " + newAuraTarget.getLogName());
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }
            return true;
        }
        return false;
    }
}
