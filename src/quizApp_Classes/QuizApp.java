package quizApp_Classes;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.18.0.3228 modeling language!*/


import java.util.*;

import OCSF.ChatClient;


public class QuizApp
{
  @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface umplesourcefile{int[] line();String[] file();int[] javaline();int[] length();}

  //------------------------
  // MEMBER VARIABLES
  //------------------------
private String clientId;
private String host;
private int port;
private Boolean initialized = false;
  
  //QuizApp Associations
  private ChatClient chatClient;
  private List<Quiz> quizzes;

  //------------------------
  // CONSTRUCTOR
  //------------------------


  public QuizApp(String aClientId) {
	

		//SET CLIENT ID:
		if(aClientId != "")
		{
			setClientId(aClientId);
		}
		else
		{
			setClientId("Android1");
		}

		quizzes = new ArrayList<Quiz>();

}

  
  //------------------------
  // INTERFACE
  //------------------------


public ChatClient getChatClient()
  {
    return chatClient;
  }

  public Quiz getQuiz(int index)
  {
    Quiz aQuiz = quizzes.get(index);
    return aQuiz;
  }

  public List<Quiz> getQuizzes()
  {
    List<Quiz> newQuizzes = Collections.unmodifiableList(quizzes);
    return newQuizzes;
  }

  public int numberOfQuizzes()
  {
    int number = quizzes.size();
    return number;
  }

  public boolean hasQuizzes()
  {
    boolean has = quizzes.size() > 0;
    return has;
  }

  public int indexOfQuiz(Quiz aQuiz)
  {
    int index = quizzes.indexOf(aQuiz);
    return index;
  }

  public boolean setChatClient(ChatClient aNewChatClient)
  {
    boolean wasSet = false;
    if (aNewChatClient != null)
    {
      chatClient = aNewChatClient;
      wasSet = true;
    }
    return wasSet;
  }

  public static int minimumNumberOfQuizzes()
  {
    return 0;
  }

  public Quiz addQuiz(int aId, String aName, int aTotalViewed, int aTotalCorrect)
  {
    return new Quiz(aId, aName, aTotalViewed, aTotalCorrect, this);
  }

  public boolean addQuiz(Quiz aQuiz)
  {
    boolean wasAdded = false;
    if (quizzes.contains(aQuiz)) { return false; }
    QuizApp existingQuizApp = aQuiz.getQuizApp();
    boolean isNewQuizApp = existingQuizApp != null && !this.equals(existingQuizApp);
    if (isNewQuizApp)
    {
      aQuiz.setQuizApp(this);
    }
    else
    {
      quizzes.add(aQuiz);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeQuiz(Quiz aQuiz)
  {
    boolean wasRemoved = false;
    //Unable to remove aQuiz, as it must always have a quizApp
    if (!this.equals(aQuiz.getQuizApp()))
    {
      quizzes.remove(aQuiz);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  public boolean addQuizAt(Quiz aQuiz, int index)
  {  
    boolean wasAdded = false;
    if(addQuiz(aQuiz))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfQuizzes()) { index = numberOfQuizzes() - 1; }
      quizzes.remove(aQuiz);
      quizzes.add(index, aQuiz);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveQuizAt(Quiz aQuiz, int index)
  {
    boolean wasAdded = false;
    if(quizzes.contains(aQuiz))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfQuizzes()) { index = numberOfQuizzes() - 1; }
      quizzes.remove(aQuiz);
      quizzes.add(index, aQuiz);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addQuizAt(aQuiz, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    chatClient = null;
    for(int i=quizzes.size(); i > 0; i--)
    {
      Quiz aQuiz = quizzes.get(i - 1);
      aQuiz.delete();
    }
  }


public void display(String msg) {
	//Display the message
			System.out.println(msg);
}


public synchronized void setInitialized(boolean b) {
	this.initialized = b;
}



public String getClientId() {
	return clientId;
}


public void setClientId(String clientId) {
	this.clientId = clientId;
}


public int getPort() {
	return port;
}


public void setPort(int port) {
	this.port = port;
}


public String getHost() {
	return host;
}


public void setHost(String host) {
	this.host = host;
}


public synchronized boolean isInitialized() {
	// TODO Auto-generated method stub
	return initialized;
}

}