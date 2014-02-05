package quizApp_Classes;

/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.18.0.3228 modeling language!*/



// line 22 "model.ump"
public class Question
{
	@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	public @interface umplesourcefile{int[] line();String[] file();int[] javaline();int[] length();}

	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Question Attributes
	private int id;
	private String questionText;
	private String answerText;
	private static int questionId = 0;

	//Question Associations
	private Quiz quiz;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public Question(String aQuestionText, String aAnswerText, Quiz aQuiz)
	{

		id = ++questionId;
		questionText = aQuestionText;
		answerText = aAnswerText;
		boolean didAddQuiz = setQuiz(aQuiz);
		if (!didAddQuiz)
		{
			throw new RuntimeException("Unable to create question due to quiz");
		}
	}
	public Question(int aId, String aQuestionText, String aAnswerText, Quiz aQuiz)
	{

		id = aId;
		questionText = aQuestionText;
		answerText = aAnswerText;
		boolean didAddQuiz = setQuiz(aQuiz);
		if (!didAddQuiz)
		{
			throw new RuntimeException("Unable to create question due to quiz");
		}

		if(aId > questionId)
		{
			questionId = aId;
		}
	}
	//------------------------
	// INTERFACE
	//------------------------

	public boolean setId(int aId)
	{
		boolean wasSet = false;
		id = aId;
		wasSet = true;
		return wasSet;
	}

	public boolean setQuestionText(String aQuestionText)
	{
		boolean wasSet = false;
		questionText = aQuestionText;
		wasSet = true;
		return wasSet;
	}

	public boolean setAnswerText(String aAnswerText)
	{
		boolean wasSet = false;
		answerText = aAnswerText;
		wasSet = true;
		return wasSet;
	}

	public int getId()
	{
		return id;
	}

	public String getQuestionText()
	{
		return questionText;
	}

	public String getAnswerText()
	{
		return answerText;
	}

	public Quiz getQuiz()
	{
		return quiz;
	}

	public boolean setQuiz(Quiz aQuiz)
	{
		boolean wasSet = false;
		if (aQuiz == null)
		{
			return wasSet;
		}

		Quiz existingQuiz = quiz;
		quiz = aQuiz;
		if (existingQuiz != null && !existingQuiz.equals(aQuiz))
		{
			existingQuiz.removeQuestion(this);
		}
		quiz.addQuestion(this);
		wasSet = true;
		return wasSet;
	}

	public void delete()
	{
		Quiz placeholderQuiz = quiz;
		this.quiz = null;
		placeholderQuiz.removeQuestion(this);
	}


	public String toString()
	{
		String outputString = "";
		return super.toString() + "["+
				"id" + ":" + getId()+ "," +
						"questionText" + ":" + getQuestionText()+ "," +
						"answerText" + ":" + getAnswerText()+ "," +
						"  " + "quiz = "+(getQuiz()!=null?Integer.toHexString(System.identityHashCode(getQuiz())):"null")
						+ outputString;
	}
}