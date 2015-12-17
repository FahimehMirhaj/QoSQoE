
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


public class SSH {
	
	// info_commands are those which return you some info after running the program. Such as "which java" or "ls -l"
	// sample code: http://www.pixelstech.net/article/1418375956-Remote-execute-command-in-Java-example
	public static String run_simple_command(String host, String user, String password, String command) {
		String result = "";
		try{             
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);
            session.connect();
             
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
             
            InputStream input = channel.getInputStream();
            channel.connect();
            
            try{
                InputStreamReader inputReader = new InputStreamReader(input);
                BufferedReader bufferedReader = new BufferedReader(inputReader);
                String line = ""; 
                while((line = bufferedReader.readLine()) != null) {
                	result += (line + "\n");
                }
                bufferedReader.close();
                inputReader.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
             
            channel.disconnect();
            session.disconnect();
        }catch(Exception ex){
            ex.printStackTrace();
        }
		return result;
	}
	
	
	public static void run_sudo_command(String host, String user, String password, String command) {
		try {
			JSch jsch=new JSch();
			Session session=jsch.getSession(user, host, 22);
		      
			UserInfo ui = new ClientUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("exec");
		   
		      // man sudo
		      //   -S  The -S (stdin) option causes sudo to read the password from the
		      //       standard input instead of the terminal device.
		      //   -p  The -p (prompt) option allows you to override the default
		      //       password prompt and use a custom one.
		    ((ChannelExec)channel).setCommand("sudo -S -p " + command);
	
	
		    InputStream in=channel.getInputStream();
		    OutputStream out=channel.getOutputStream();
		    ((ChannelExec)channel).setErrStream(System.err);
	
		    channel.connect();
	
		    out.write((password+"\n").getBytes());
		    out.flush();
	
		    byte[] tmp=new byte[1024];
		    while(true) {
		    	while(in.available() > 0) {
				   
		    		int i = in.read(tmp, 0, 1024);
				    if(i < 0) break;
//				    System.out.print("HH" + new String(tmp, 0, i));
			   }
			   if(channel.isClosed()) {
				   System.out.println("exit-status: "+channel.getExitStatus());
				   break;
			   }
		        try {
		        	Thread.sleep(1000);
		        } catch(Exception ee) {
		        	ee.printStackTrace();
		        }
		      }
		      channel.disconnect();
		      session.disconnect();
	    }
	    catch(Exception e){
	      System.out.println(e);
	    }
	}
	
	public static void run_sudo_command_interactive(String host, String user, String password, String command) {
		try {
			JSch jsch=new JSch();
			Session session=jsch.getSession(user, host, 22);
		      
			UserInfo ui = new ClientUserInfo(password);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("exec");
		   
		      // man sudo
		      //   -S  The -S (stdin) option causes sudo to read the password from the
		      //       standard input instead of the terminal device.
		      //   -p  The -p (prompt) option allows you to override the default
		      //       password prompt and use a custom one.
		    ((ChannelExec)channel).setCommand("sudo -S -p " + command);
	
	
		    InputStream in=channel.getInputStream();
		    OutputStream out=channel.getOutputStream();
		    ((ChannelExec)channel).setErrStream(System.err);
	
		    channel.connect();
	
		    out.write((password+"\n").getBytes());
		    out.write(("Y"+"\n").getBytes());
		    out.flush();
	
		    byte[] tmp=new byte[1024];
		    while(true) {
		    	while(in.available() > 0) {
				   
		    		int i = in.read(tmp, 0, 1024);
				    if(i < 0) break;
				    System.out.print(new String(tmp, 0, i));
			   }
			   if(channel.isClosed()) {
				   System.out.println("exit-status: "+channel.getExitStatus());
				   break;
			   }
		        try {
		        	Thread.sleep(1000);
		        } catch(Exception ee) {
		        	ee.printStackTrace();
		        }
		      }
		      channel.disconnect();
		      session.disconnect();
	    }
	    catch(Exception e){
	      System.out.println(e);
	    }
	}
	
	
}
