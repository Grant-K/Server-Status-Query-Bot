package Query.Bot;

import java.net.InetSocketAddress;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

import java.io.*;

@SuppressWarnings("unused")
public class App extends ListenerAdapter
{
	public static String cwd = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
	public static String confPath = (cwd + "/LNbot/config.txt");
	public static String tokenPath = (cwd + "/LNbot/token.txt");
	public String statusRoom;
	public FileReader fr = null;
  	public BufferedReader br = null;
  	public FileWriter fw = null;
  	public PrintWriter pw = null;
  	public SourceServer serverInfo = null;
  	public CompletableFuture<List<SourcePlayer>> playerInfoFuture = null;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, LoginException, IOException
    {
		System.out.println(cwd);
		System.out.println(confPath);
		String token= null;
		try {
			FileReader frs = new FileReader(tokenPath);
			BufferedReader brs = new BufferedReader(frs);
			String input = brs.readLine();
			if(input != null)
				token = input;
			brs.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
		JDA jdaBot = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
    	jdaBot.addEventListener(new App());
    	System.out.println("Bot Online");
    	App driver = new App();
    	driver.setStatusRoom();
    }
	
	public void setStatusRoom()
	{
	  	statusRoom = "servers-status";
		try {
			fr = new FileReader(confPath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if(line != null)
				statusRoom = line;
			br.close();
			fw = new FileWriter(confPath);
			pw = new PrintWriter(fw);
			pw.println(statusRoom);
			pw.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
		
	}
	
	public void setStatusRoom(String arg)
	{
	  	statusRoom = "servers-status";
	  	try {
			fw = new FileWriter(confPath);
			pw = new PrintWriter(fw);
			pw.println(arg);
			pw.close();
		} catch (IOException e1) {
		}
	  	
		try {
			fr = new FileReader(confPath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if(line != null)
				statusRoom = line;
			br.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
	}
	
	public void getStatusRoom() 
	{
		statusRoom = "servers-status";
		try {
			fr = new FileReader(confPath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if(line != null)
				statusRoom = line;
			br.close();
		} catch (FileNotFoundException e2) {
			System.out.println("Error!");
		} catch (IOException e1) {
			System.out.println("Error!");
		}
	}
	
	public boolean queryServer(String ip, int port)
	{
		@SuppressWarnings("resource")
    	SourceQueryClient sourceQueryClient = new SourceQueryClient();
        InetSocketAddress serverAddress = new InetSocketAddress(ip, port);
        try{
        		serverInfo = sourceQueryClient.getServerInfo(serverAddress).get();
        		playerInfoFuture = sourceQueryClient.getPlayers(serverAddress);
            	playerInfoFuture.whenComplete((players, playerError) -> {
            	
            });
            	return true;
        	} catch (ExecutionException e1)
        	{
        		return false;
        	} catch (InterruptedException e1) {
        		return false;
			}
	}
	
	public String formatSeconds(double seconds)
	{
		String formatedTime = null;
		int sec = (int)seconds;
		int min = sec/60%60;
		int hour = sec/60/60;
		sec = (int)seconds%60;
		if(hour >= 1)
			formatedTime = String.format("%02d:%02d:%02d", hour, min, sec);
		else
			formatedTime = String.format("%02d:%02d", min, sec);
		System.out.println("Time has been formated from " + sec + " seconds to " + formatedTime);
		return formatedTime;
	}
	
    public void onMessageReceived(MessageReceivedEvent e) 
    {
    	if (e.getAuthor().isBot()) return;
        //Obtains properties of the received message
        Message objMsg = e.getMessage();
        MessageChannel objChannel = e.getChannel();
        User objUser = e.getAuthor();
        String channelName = objChannel.getName();
        String[] messageParts = objMsg.getContentRaw().split("\\s");
        String command = messageParts[0];
        String arg = null;
        if(messageParts.length > 1)
        	{
        		arg = messageParts[1];
        		if(messageParts.length > 2)
        		{
	        		for(int i = 2; i < messageParts.length; i++)
	        			arg = arg + messageParts[i];	
        		}
        	}
    	setStatusRoom();
        if(channelName.equals(statusRoom) && (objUser.isBot()) == false)
        	{
	        	System.out.println("Message Recived! Contents is as follows:");
	        	System.out.println(objMsg.getContentRaw());
	        	System.out.println("Author is: " + objUser.getName());
        	}
        String SpaceEngineersip = "107.150.63.18";
        int SpaceEngineersPort = 27016;
    	boolean SpaceEngineersup = queryServer(SpaceEngineersip, SpaceEngineersPort);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        if(command.substring(0,1).equals("^") && (objUser.isBot()) == false)
        	{
        		System.out.println("Mesage initation character entered! Reply is as follows!");
        		if (command.toLowerCase().equals("^serverstatus") && channelName.equals(statusRoom))
        		{
        			System.out.println("Command: " + command + "\nArg(s): " + arg);
        			if(arg == null || arg == "" || arg == " " || arg.toLowerCase().equals("all") || arg.toLowerCase().equals("both"))
        			{
        				if(SpaceEngineersup)
	        				{
		    	    			objChannel.sendMessage("The SpaceEngineers Server is Online!\n").complete();
		    	    			System.out.println("The SpaceEngineers Server is Online!");
		    	    			objChannel.sendMessage("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers()).complete();
		    	    			System.out.println("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers());
		    	    			List<SourcePlayer> players = null;
								try {
									players = playerInfoFuture.get();
								} catch (InterruptedException e1) {
									System.out.println("Error!");
								} catch (ExecutionException e1) {
									System.out.println("Error!");
								}
								if(players.size() >= 1)
								{
									objChannel.sendMessage("(Time Formated as HH:MM:SS)\nOnline Player(s) Info:").complete();
									System.out.println("(Time Formated as HH:MM:SS)\nOnline Player(s) Info:");
								
								 int playersOnline = players.size();
								 String playersPrint = null;
		    	    			 for(int i = 0; i < playersOnline; i++)
		    	    			 {
		    	    				 String playerName = players.get(i).getName();
		    	    				 double playerTimeSeconds = players.get(i).getDuration();
		    	    				 System.out.println(playerTimeSeconds);
		    	    				 String playersTimeOnline = formatSeconds(playerTimeSeconds);
		    	    				 if(i == 0)
		    	    					 playersPrint = "Username: " + playerName + "\nTime Online: " + playersTimeOnline + "\n";
		    	    				 else
		    	    				 playersPrint = playersPrint + "\nUsername: " + playerName + "\nTime Online: " + playersTimeOnline + "\n";
		    	    			 }
		    	    				 objChannel.sendMessageFormat(playersPrint).queue();
		    	    			 	 System.out.println(playersPrint);
								 }
		    	    		}
        				         else 
        				         {
			    	    			 objChannel.sendMessageFormat("The SpaceEngineers Server is Offline!").complete();
			    	    			 System.out.println("The SpaceEngineers Server is Offline!");
		    	    			 }
		        	 }
        			 else if(arg.toLowerCase().equals("spaceengineers"))
		        	 {
        				 if(SpaceEngineersup)
		        		 {
        					 objChannel.sendMessage("The SpaceEngineers Server is Online!\n").complete();
						     System.out.println("The SpaceEngineers Server is Online!");
						     objChannel.sendMessage("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers()).complete();
						     System.out.println("ServerName: " + serverInfo.getName() + " \nPlayers: " + serverInfo.getNumOfPlayers() + "/" + serverInfo.getMaxPlayers());
						     List<SourcePlayer> players = null;
							 try 
							 {
								 players = playerInfoFuture.get();
							 } 
							 catch (InterruptedException e1) 
							 {
								 System.out.println("Error!");
							 } 
							 catch (ExecutionException e1) 
							 {
								 System.out.println("Error!");
							 }
							 if(players.size() >= 1)
								{
									objChannel.sendMessage("(Time Formated as HH:MM:SS)\nOnline Player(s) Info:").complete();
									System.out.println("(Time Formated as HH:MM:SS)\nOnline Player(s) Info:");
								
								 int playersOnline = players.size();
								 String playersPrint = null;
		    	    			 for(int i = 0; i < playersOnline; i++)
		    	    			 {
		    	    				 String playerName = players.get(i).getName();
		    	    				 double playerTimeSeconds = players.get(i).getDuration();
		    	    				 System.out.println(playerTimeSeconds);
		    	    				 String playersTimeOnline = formatSeconds(playerTimeSeconds);
		    	    				 if(i == 0)
		    	    					 playersPrint = "Username: " + playerName + "\nTime Online: " + playersTimeOnline + "\n";
		    	    				 else
		    	    				 playersPrint = playersPrint + "\nUsername: " + playerName + "\nTime Online: " + playersTimeOnline + "\n";
		    	    			 }
		    	    				 objChannel.sendMessageFormat(playersPrint).queue();
		    	    			 	 System.out.println(playersPrint);
								 }
		        		  }
		        		  else 
		        		  {
		        			  objChannel.sendMessage("The SpaceEngineers Server is Offline!").complete();
		        			  System.out.println("The  SpaceEngineers Server is Offline!");
		        		  }
		        	 }
		         }
		         else if (command.equals("^ServerStatus") && (arg != null && arg != "" && arg != " "))
		         {
		        	 objChannel.sendMessage("Please enter the command with the server you wish to check afterwards (^ServerStatus SpaceEngineers)").complete();
		        	 System.out.println("Please enter the command with the server you wish to check afterwards (^ServerStatus SpaceEngineers)");
		         }
		         else
		         {
		        	 if(command.toLowerCase().equals("^help"))// && rolePos >= 32)
			         {
		        		 objChannel.sendMessage("Currently avaliable command(s) are: ^ServerStatus (ServerName) which checks to see if the desired server is online.").complete();
			        	 System.out.println("Currently avaliable command(s) are: ^ServerStatus (ServerName) which checks to see if the desired server is online.");
			         }
			         if(command.toLowerCase().equals("^setstatuschannel"))
			         {
			        	 if(arg == null)
				         {
			        		 objChannel.sendMessage("Please enter the command with the channel you wish to set for server status afterswords! I.E. (^SetStatusChannel server-status)").complete();
				        	 System.out.println("Please enter the command with the channel you wish to set for server status afterswords! I.E. (^SetStatusChannel server-status)");
				         }
			        	 else
			        	 {			
			        		 setStatusRoom(arg);
			        		 objChannel.sendMessage("Server status channel is now set to: " + statusRoom).complete();
			        		 System.out.println("Server status channel is now set to: " + statusRoom);
			        	 }
			          }
			          else if(command.toLowerCase().equals("^getstatuschannel"))
			          {
			        	  getStatusRoom();
			        	  objChannel.sendMessage("The server status channel is curently set to: " + statusRoom).complete();
			    		  System.out.println("The server status channel is curently set to: " + statusRoom);
			          }
	        		  else
	        		  {
	        			  objChannel.sendMessage("Please do ^help for help!").complete();
	        			  System.out.println("Please do ^help for help!");
			          }
		           }
        		//objMsg.delete().queueAfter(5, TimeUnit.SECONDS);
        	 }
    	}
}
