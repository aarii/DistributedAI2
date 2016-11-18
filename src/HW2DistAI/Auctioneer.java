package HW2DistAI;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 * Created by Araxi and Amir on 2016-11-17.
 */
public class Auctioneer extends Agent {
    private AID a1 = new AID("Auctioneer", AID.ISLOCALNAME);

    int startPrice;
    int bottomPrice;
    AID [] bidderAgents;
    ACLMessage receivedMsg;
    int proposeCount = 0;

    @Override
    protected void setup() {
        System.out.println("Hello " + getLocalName());

        getAllBidders();
        setStartAndBottomPrice();
        informBidders();

        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                receivedProposalMsgs();
            }
        });
    }

    protected void getAllBidders(){
        try {
               /* Subscribe Artifacts service by Curator agent */
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
                System.out.println("The bidders are: " + bidderAgents[i].getName());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }

    }
    protected void setStartAndBottomPrice(){
        final Object[] args = getArguments();

        String sp = (String) args[0];
        String bp = (String) args[1];

        startPrice = Integer.parseInt(sp);
        bottomPrice = Integer.parseInt(bp);

        System.out.println("Start price: " + startPrice);
        System.out.println("Bottom price: " + bottomPrice);

    }
    protected void informBidders(){
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        for(int i = 0; i<bidderAgents.length; i++){
            inform.addReceiver(bidderAgents[i]);
        }
        inform.setContent(String.valueOf(startPrice));
        inform.setConversationId("The-auction");
        send(inform);

    }
    protected void receivedProposalMsgs() {
        receivedMsg = receive();
        if(receivedMsg != null) {
            if (receivedMsg.getPerformative() == 11) {


                if(startPrice <= bottomPrice){
                    System.out.println("Came to Bottom Price and ended the auction");

                    return;

                }

                System.out.println(getLocalName() + " received " + receivedMsg.getContent() + " from a bidder");
                if (proposeCount != bidderAgents.length) {
                    if (receivedMsg.getContent().equalsIgnoreCase("accept")) {
                        AID winner = receivedMsg.getSender();
                        ACLMessage replyToWinner = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        replyToWinner.addReceiver(winner);
                        replyToWinner.setContent("won!!");
                        send(replyToWinner);
                        this.doDelete();

                    }

                    if (receivedMsg.getContent().equalsIgnoreCase("reject")) {
                        proposeCount++;
                    }
                    if (proposeCount == bidderAgents.length) {
                        proposeCount = 0;
                        decreasePrice();
                        startOver();
                    }



                }
            }
        }
    }

    @Override
    protected void takeDown() {
       // System.out.println("Shutting down now");
        super.takeDown();
    }

    private void decreasePrice(){
        startPrice = startPrice - (int)(startPrice * 0.10);
    }
    private void startOver(){
        informBidders();
    }


}
