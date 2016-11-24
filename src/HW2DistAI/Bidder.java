package HW2DistAI;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by araxi on 2016-11-17.
 */
public class Bidder extends Agent {

   //  private AID a1 = new AID("Bidder", AID.ISLOCALNAME);
    int maxPrice = 0;
    ACLMessage receivedMsg;
    boolean x = true;

    @Override
    protected void setup() {
        System.out.println("Hello " + getLocalName());
        createServiceInDF();
        receivedMsg = blockingReceive();
            getInformMsgs();

        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {

                receivedMsg = receive();
                if(receivedMsg != null) {
                    getCFPMsgs();
                    if (x == true) {
                        selectMaxPriceToPay();
                        x = false;
                    }
                    getAcceptedProposal();
                    sendProposeMsg();
                }
            }
        });
    }

    protected void createServiceInDF(){

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

    protected  void getInformMsgs(){
        if( receivedMsg.getPerformative()== ACLMessage.INFORM){
            System.out.println(getLocalName() + " received an inform message saying: " + receivedMsg.getConversationId());
        }
    }

    protected void getCFPMsgs(){
        if(receivedMsg.getPerformative()== ACLMessage.CFP){
            System.out.println(getLocalName() + " received an cfp message  with item price: " + receivedMsg.getContent());
            //  System.out.println("Item price is: " + receivedMsg.getContent());
        }
    }

    protected void selectMaxPriceToPay(){
            int temp = ThreadLocalRandom.current().nextInt(1, 100 + 1);
            int tempPrice = Integer.parseInt(receivedMsg.getContent());
            maxPrice = (tempPrice / 100) * temp;
            System.out.println(getLocalName() + " can pay " + temp + "% of the original price. Therefore the price the bidder can pay is: " + maxPrice);

    }

    protected void getAcceptedProposal(){
        if(receivedMsg.getPerformative() == ACLMessage.INFORM){
            System.out.println(getLocalName() + " " + receivedMsg.getContent());
            this.doDelete();
        }
    }

    protected void sendProposeMsg(){
        ACLMessage proposeMsg = new ACLMessage();
        int itemValue = 0;
        try {
            itemValue = Integer.parseInt(receivedMsg.getContent());
        }catch (Exception e){
            doDelete();
        }
        if(itemValue <= maxPrice){
            proposeMsg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        }
        if(itemValue > maxPrice){
            proposeMsg.setPerformative(ACLMessage.REJECT_PROPOSAL);
        }

        AID auctioneer = receivedMsg.getSender();
        proposeMsg.addReceiver(auctioneer);
        send(proposeMsg);

    }


}


