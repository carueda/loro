package loroedi.jedit;

/////////////////////////////////////////////////////////////////
/*
    Modificado por carueda, 2001-10-09:
        -Manejo de coloring especial para prompt y prefix.

    Modificado por carueda, 2001-01-29:
        -se ignoran comillas simples para evitar resaltado
         de error ante variables semanticas como v'
        -la idea es emular lo hecho para labels pero falta
         permitir que el identificador comience en cualquier
         columna (no solo en la primera).
*/

/*
 * CTokenMarker.java - C token marker
 * Copyright (C) 1998, 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import javax.swing.text.Segment;

/**
 * C token marker.
 *
 * @author Slava Pestov
 * @version $Id$
 */
public class CTokenMarker extends TokenMarker
{
	//carueda
	protected String prompt = null;
	protected int prompt_len;
	protected String prefix_special = null;
	protected int prefix_special_len;
	protected String prefix_invalid = null;
	protected int prefix_invalid_len;

	// private members
	private boolean cpp;
	private KeywordMap keywords;
	private int lastOffset;
	private int lastKeyword;

	public CTokenMarker(boolean cpp, KeywordMap keywords)
	{
		this.cpp = cpp;
		this.keywords = keywords;
	}
	private boolean doKeyword(Segment line, int i, char c)
	{
		int i1 = i+1;

		int len = i - lastKeyword;
		byte id = keywords.lookup(line,lastKeyword,len);
		if(id != Token.NULL)
		{
			if(lastKeyword != lastOffset)
				addToken(lastKeyword - lastOffset,Token.NULL);
			addToken(len,id);
			lastOffset = i;
		}
		lastKeyword = i1;
		return false;
	}

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
		int length = line.count + offset;
		boolean backslash = false;

loop:
		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			char c = array[i];


			if(c == '\\')
			{
				backslash = !backslash;
				continue;
			}

//////////////////////////////////////////////////////////////////////////////////////
// carueda
			if ( prompt != null )
			{
				if ( lastKeyword == offset && line.count >= prompt_len )
				{
					String possible_prompt = new String(array, offset, prompt_len);
					if ( possible_prompt.equals(prompt) )
					{
						i1 += prompt_len - 1;
						addToken(i1 - lastOffset, Token.PROMPT);
						lastOffset = lastKeyword = i1;
						continue;
					}
				}
			}

			if ( prefix_special != null )
			{
				// si la linea empieza con prefix_special, se le pone estilo
				// normal a toda la linea:
				if ( lastKeyword == offset && line.count >= prefix_special_len )
				{
					String possible_prefix = new String(array, offset, prefix_special_len);
					if ( possible_prefix.equals(prefix_special) )
					{
						addToken(i - lastOffset,token);
						addToken(length - i,Token.SPECIAL);
						lastOffset = lastKeyword = length;

						// anule efecto previo:
						token = Token.NULL;

						break loop;
					}
				}
			}
			if ( prefix_invalid != null )
			{
				// si la linea empieza con prefix_invalid, se le pone estilo
				// invalido a toda la linea:
				if ( lastKeyword == offset && line.count >= prefix_invalid_len )
				{
					String possible_prefix = new String(array, offset, prefix_invalid_len);
					if ( possible_prefix.equals(prefix_invalid) )
					{
						addToken(i - lastOffset,token);
						addToken(length - i,Token.INVALID);
						lastOffset = lastKeyword = length;

						// anule efecto previo:
						token = Token.NULL;

						break loop;
					}
				}
			}
//////////////////////////////////////////////////////////////////////////////////////


			switch(token)
			{
			case Token.NULL:

				switch(c)
				{
				case '#':
					if(backslash)
						backslash = false;
					else if(cpp)
					{
						if(doKeyword(line,i,c))
							break;
						addToken(i - lastOffset,token);
						addToken(length - i,Token.KEYWORD2);
						lastOffset = lastKeyword = length;
						break loop;
					}
					break;

			/*************NO SE MANEJAN LABELS
				case ':':
					if(lastKeyword == offset)
					{
						if(doKeyword(line,i,c))
							break;
						backslash = false;
						addToken(i1 - lastOffset,Token.LABEL);
						lastOffset = lastKeyword = i1;
					}
					else if(doKeyword(line,i,c))
						break;
					break;
			*************/


			/************** PENDIENTE MANEJO DE LITERALES CADENA Y
							VARIABLES SEMANTICAS (2001-02-02)
				case '\'':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL2;
						lastOffset = lastKeyword = i;
					}
					break;
			**************/

				case '/':
					backslash = false;
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '*':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							if(length - i > 2 && array[i+2] == '*')
								token = Token.COMMENT2;
							else
								token = Token.COMMENT1;
							break;
						case '/':
							addToken(i - lastOffset,token);
							addToken(length - i,Token.COMMENT1);
							lastOffset = lastKeyword = length;
							break loop;
						}
					}
					break;
					
// carueda: para DOC
				case '\'':
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '\'':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							token = Token.DOC;
							break;
						}
					}
					break;
					
// carueda: para GUIDE, IMPL
				case '{':
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '{':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							token = Token.GUIDE;
							break;
						case '%':
							addToken(i - lastOffset,token);
							lastOffset = lastKeyword = i;
							token = Token.IMPL;
							break;
						}
					}
					break;
					
					
				case '"':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL1;
						lastOffset = lastKeyword = i;
					}
					break;

				default:
					backslash = false;
					if ( !Character.isLetterOrDigit(c) && c != '_' )
						doKeyword(line,i,c);
					break;
				}
				break;

			case Token.COMMENT1:
			case Token.COMMENT2:
				backslash = false;
				if(c == '*' && length - i > 1)
				{
					if(array[i1] == '/')
					{
						i++;
						addToken((i+1) - lastOffset,token);
						token = Token.NULL;
						lastOffset = lastKeyword = i+1;
					}
				}
				break;

			case Token.DOC:
				if(c == '\'' && length - i > 1)
				{
					if(array[i1] == '\'')
					{
						i++;
						addToken((i+1) - lastOffset,token);
						token = Token.NULL;
						lastOffset = lastKeyword = i+1;
					}
				}
				break;
				
			case Token.GUIDE:
				if(c == '}' && length - i > 1)
				{
					if(array[i1] == '}')
					{
						i++;
						addToken((i+1) - lastOffset,token);
						token = Token.NULL;
						lastOffset = lastKeyword = i+1;
					}
				}
				break;
				
			case Token.IMPL:
				if(c == '%' && length - i > 1)
				{
					if(array[i1] == '}')
					{
						i++;
						addToken((i+1) - lastOffset,token);
						token = Token.NULL;
						lastOffset = lastKeyword = i+1;
					}
				}
				break;
				
			case Token.LITERAL1:
				if(backslash)
					backslash = false;
				else if(c == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			case Token.LITERAL2:
				if(backslash)
					backslash = false;
				else if(false && c == '\'')
				{
					addToken(i1 - lastOffset,Token.LITERAL1);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			default:
				throw new InternalError("Invalid state: "
					+ token);
			}
		}

		if(token == Token.NULL)
			doKeyword(line,length,'\0');

		switch(token)
		{

	/****
		PENDIENTE 'x', n'
		Los literales cadena pueden abarcar varias lineas

		case Token.LITERAL1:
		//case Token.LITERAL2:
			addToken(length - lastOffset,Token.INVALID);
			token = Token.NULL;
		break;
	****/
		case Token.KEYWORD2:
			addToken(length - lastOffset,token);
			if(!backslash)
				token = Token.NULL;
		default:
			addToken(length - lastOffset,token);
			break;
		}

		return token;
	}
}