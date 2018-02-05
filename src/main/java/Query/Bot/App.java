package Query.Bot;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.security.auth.login.LoginException;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourcePlayer;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
/**
 * Hello world!
 *
 */
public class App extends ListenerAdapter
{
	public static void main(String[] args) throws InterruptedException, ExecutionException, LoginException
    {
		JDA jdaBot = new JDABuilder(AccountType.BOT).setToken("NDEwMTc0NjI1OTg1NTkzMzY0.DVpUZw.KD6cm_3h_BvhTaRVoFlsBCRtj6A").buildBlocking();
    	jdaBot.addEventListener(new App());
    	System.out.println("Bot Online");
    	
    }
    	public void onMessageReceived(MessageReceivedEvent e) {
        	
        	//Obtains properties of the received message
        	Message objMsg = e.getMessage();
        	MessageChannel objChannel = e.getChannel();
        	User objUser = e.getAuthor();
        	
        	//Responds to any user who says "hello"
        	List<Role> roles = objUser.getJDA().getRoles();
        	int rolePos = -1;
        	for(int i = 0; i < roles.size(); i++)
        	{
        		int testPos = roles.get(i).getPosition();
        		if(testPos > rolePos)
        		{
        			rolePos = testPos;
        		}
        	}
        	String channelName = objChannel.getName();
        	String[] messageParts = objMsg.getContentRaw().split("\\s");
        	String command = messageParts[0];
        	String arg = null;
        	if(messageParts.length > 1)
        	{
        		arg = messageParts[1];
        	}
        	if(channelName.equals("servers-status") && (objUser.getName().equals("QueryDevTest")) == false)
        	{
	        	System.out.println("Message Recived! Contents is as follows:");
	        	System.out.println(objMsg.getContentRaw());
	        	System.out.println("Author is: " + objUser.getName());
        	}
        	String SpaceEngineersip = "107.150.63.18";
    		boolean SpaceEngineersup = false;
    		@SuppressWarnings("resource")
    		SourceQueryClient sourceQueryClient = new SourceQueryClient();
        	InetSocketAddress serverAddress = new InetSocketAddress(SpaceEngineersip, 27016);
        	SourceServer serverInfo = null;
        	CompletableFuture<List<SourcePlayer>> playerInfoFuture = null;
        	try{
        		serverInfo = sourceQueryClient.getServerInfo(serverAddress).get();
        		playerInfoFuture = sourceQueryClient.getPlayers(serverAddress);
            	playerInfoFuture.whenComplete((players, playerError) -> {
            	
            	});
        		SpaceEngineersup = true;
        	} catch (ExecutionException e1)
        	{
        		SpaceEngineersup = false;
        	} catch (InterruptedException e1) {
        		SpaceEngineersup = false;
			}
        	DecimalFormat numberFormat = new DecimalFormat("#.00");
        	if(command.substring(0,1).equals("^"))
        		System.out.println("Meesage initation character entered! Reply is as follows!");
        	if (command.equals("^ServerStatus") && channelName.equals("servers-status")) {// && rolePos >= 32) {
        		if(arg == null || arg == "" || arg == " " || arg.toLowerCase().equals("all") || arg.toLowerCase().equals("both"))
        		{
    	    		if(SpaceEngineersup)
    	    		{
    	    			 objChannel.sendMessage("The SpaceEngineers Server is Online!").complete();
    	    			 System.out.println("The SpaceEngineers Server is Online!");
    	    			 objChannel.sendMessage("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers()).complete();
    	    			 System.out.println("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers());
    	    			 List<SourcePlayer> players = null;
						try {
							players = playerInfoFuture.get();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							e1.printStackTrace();
						}
						if(players.size() >= 1)
						{
							objChannel.sendMessage("The Online Players Are:").complete();
							System.out.println("The Online Players Are:");
						}
    	    			 for(int i = 0; i < players.size(); i++)
    	    			 {
    	    				 objChannel.sendMessage(players.get(i).getName() + "\t Time Online: " + numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)").complete();
    	    			 	 System.out.println(players.get(i).getName() + "\t Time Online: " + numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)");
    	    			 }
    	    		}else {
    	    			 objChannel.sendMessage("The  SpaceEngineers Server is Offline!").complete();
    	    			 System.out.println("The  SpaceEngineers Server is Offline!");
    	    		 }
        		}else if(arg.toLowerCase().equals("spaceengineers"))
        		{
        			if(SpaceEngineersup)
    	    		{
        			objChannel.sendMessage("The SpaceEngineers Server is Online!").complete();
	    			 System.out.println("The SpaceEngineers Server is Online!");
	    			 objChannel.sendMessage("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers()).complete();
	    			 System.out.println("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers());
	    			 List<SourcePlayer> players = null;
					try {
						players = playerInfoFuture.get();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} catch (ExecutionException e1) {
						e1.printStackTrace();
					}
					if(players.size() >= 1) {
						objChannel.sendMessage("The Online Players Are:").complete();
						System.out.println("The Online Players Are:");
					}
	    			 for(int i = 0; i < players.size(); i++)
	    			 {
	    				 objChannel.sendMessage(players.get(i).getName() + "\t Time Online: " + numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)").complete();
	    			 	 System.out.println(players.get(i).getName() + "\t Time Online: " + numberFormat.format((players.get(i).getDuration()) / 60.0) + " (min)");
	    			 }
        			}else {
    	        	objChannel.sendMessage("The SpaceEngineers Server is Offline!").complete();
        			System.out.println("The  SpaceEngineers Server is Offline!");
        			}
        		}
        	else
        		{
        			objChannel.sendMessage("Please enter the command with the server you wish to check afterwards (^ServerStatus SpaceEngineers)").complete();
        			System.out.println("Please enter the command with the server you wish to check afterwards (^ServerStatus SpaceEngineers)");
        		}
        	}else if(command.toLowerCase().equals("^help"))// && rolePos >= 32)
        	{
        		objChannel.sendMessage("Currently avaliable command(s) are: ^ServerStatus (ServerName) which checks to see if the desired server is online.").complete();
        		System.out.println("Currently avaliable command(s) are: ^ServerStatus (ServerName) which checks to see if the desired server is online.");
        	}else if(command.substring(0, 1).equals("^")) {
        		objChannel.sendMessage("Please do ^help for help!").complete();
        		System.out.println("Please do ^help for help!");
        	}//else if(rolePos < 32)
        	//{
        	//	objChannel.sendMessage("Your Role Does Not Have Access to this bot!").complete();
        	//}
        }
}
