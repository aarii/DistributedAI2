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

        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                receivedMsgs();
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

    protected void receivedMsgs(){

        receivedMsg = receive();
        if(receivedMsg != null) {
            getInformMsgs();

            if(x == true) {
                selectMaxPriceToPay();
                x = false;
            }

            getAcceptedProposal();
            sendProposeMsg();
        }
    }

    protected  void getInformMsgs(){
        if( receivedMsg.getPerformative()==7){
            System.out.println("Item price is: " + receivedMsg.getContent());
        }
    }

    protected void selectMaxPriceToPay(){
        int temp = ThreadLocalRandom.current().nextInt(1, 100 +1);
        int tempPrice = Integer.parseInt(receivedMsg.getContent());
        maxPrice = (tempPrice / 100) * temp;
        System.out.println(getLocalName() + "'s max price to pay for this item is: " + maxPrice);

    }

    protected void getAcceptedProposal(){
        if(receivedMsg.getPerformative() == 0){
            System.out.println(getLocalName() + " " + receivedMsg.getContent());
            this.doDelete();
        }
    }

    protected void sendProposeMsg(){
        ACLMessage proposeMsg = new ACLMessage(ACLMessage.PROPOSE);
        int itemValue = 0;
        try {
            itemValue = Integer.parseInt(receivedMsg.getContent());
        }catch (Exception e){
            doDelete();
        }
        if(itemValue <= maxPrice){
            proposeMsg.setContent("accept");
        }
        if(itemValue > maxPrice){
            proposeMsg.setContent("reject");
        }

        AID auctioneer = receivedMsg.getSender();
        proposeMsg.addReceiver(auctioneer);
        send(proposeMsg);

    }


}


