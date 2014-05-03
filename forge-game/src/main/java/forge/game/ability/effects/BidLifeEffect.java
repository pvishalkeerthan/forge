package forge.game.ability.effects;

import com.google.common.collect.Iterables;
import forge.game.ability.AbilityFactory;
import forge.game.ability.AbilityUtils;
import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.player.Player;
import forge.game.player.PlayerActionConfirmMode;
import forge.game.spellability.AbilitySub;
import forge.game.spellability.SpellAbility;

import java.util.ArrayList;
import java.util.List;

public class BidLifeEffect extends SpellAbilityEffect {

    /* (non-Javadoc)
     * @see forge.card.abilityfactory.SpellEffect#getStackDescription(java.util.Map, forge.card.spellability.SpellAbility)
     */
    @Override
    protected String getStackDescription(SpellAbility sa) {
        return "Bid Life";
    }

    /* (non-Javadoc)
     * @see forge.card.abilityfactory.SpellEffect#resolve(java.util.Map, forge.card.spellability.SpellAbility)
     */
    @Override
    public void resolve(SpellAbility sa) {
        final Card host = sa.getHostCard();
        final Player activator = sa.getActivatingPlayer();
        final List<Player> bidPlayers = new ArrayList<Player>();
        final int startBidding;
        if (sa.hasParam("StartBidding")) {
            String start = sa.getParam("StartBidding");
            if ("Any".equals(start)) {
                startBidding = activator.getController().announceRequirements(sa, "Choose a starting bid", true);
            } else {
                startBidding = AbilityUtils.calculateAmount(host, start, sa);
            }
        } else {
            startBidding = 0;
        }
        
        if (sa.hasParam("OtherBidder")) {
            bidPlayers.add(activator);
            bidPlayers.addAll(AbilityUtils.getDefinedPlayers(host, sa.getParam("OtherBidder"), sa));
        } else{
            bidPlayers.addAll(activator.getGame().getPlayers());
            int pSize = bidPlayers.size();
            // start with the activator
            while (bidPlayers.contains(activator) && !activator.equals(Iterables.getFirst(bidPlayers, null))) {
                bidPlayers.add(pSize - 1, bidPlayers.remove(0));
            }
        }
        boolean willBid = true;
        Player winner = activator;
        int bid = startBidding;
        while (willBid) {
            willBid = false;
            for (final Player p : bidPlayers) {
                final boolean result = p.getController().confirmBidAction(sa, PlayerActionConfirmMode.BidLife,
                        "Do you want to top bid? Current Bid =" + String.valueOf(bid), bid, winner);
                willBid |= result;
                if (result) { // a different choose number
                    bid += p.getController().chooseNumber(sa, "Bid life:", 1, 9);
                    winner = p;
                }
            }
        }
        
        host.setChosenNumber(bid);
        host.addRemembered(winner);
        final SpellAbility action = AbilityFactory.getAbility(host.getSVar(sa.getParam("BidSubAbility")), host);
        action.setActivatingPlayer(sa.getActivatingPlayer());
        ((AbilitySub) action).setParent(sa);
        AbilityUtils.resolve(action);
        host.clearRemembered();
    }
}
