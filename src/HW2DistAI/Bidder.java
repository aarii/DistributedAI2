package HW2DistAI;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 * Created by araxi on 2016-11-17.
 */
public class Bidder extends Agent {

    private AID a1 = new AID("HW2DistAI.Bidder", AID.ISLOCALNAME);
    @Override
    protected void setup() {

        final DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Auction");
        sd.setName("Auction on something");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }


    }

}
