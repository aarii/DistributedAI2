package HW2DistAI;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 * Created by araxi on 2016-11-17.
 */
public class Auctioneer extends Agent {
    private AID a1 = new AID("HW2DistAI.Auctioneer", AID.ISLOCALNAME);

    int startPrice;
    int bottomPrice;
    AID [] bidderAgents;

    @Override
    protected void setup() {
        final Object[] args = getArguments();
        System.out.println("Hello " + a1.getName());
        String sp = (String) args[0];
        String bp = (String) args[1];

        startPrice = Integer.parseInt(sp);
        bottomPrice = Integer.parseInt(bp);

        System.out.println("Start price: " + startPrice);
        System.out.println("Bottom price: " + bottomPrice);

         /* find all HW2DistAI.Bidder agents */
        try {
               /* subscribe Artifacts service by Curator agent */
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();

            sd.setType("Auction");
            dfd.addServices(sd);
            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));
            send(DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, sc));
            DFAgentDescription[] result = DFService.search(this, dfd);
            bidderAgents = new AID[result.length];

            for (int i = 0; i < bidderAgents.length; i++) {
                bidderAgents[i] = result[i].getName();
                System.out.println("bidders är: " + bidderAgents[i].toString());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void takeDown() {
        super.takeDown();
    }
}
