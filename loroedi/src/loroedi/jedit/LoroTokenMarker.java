package loroedi.jedit;

import loro.Loro;
import loro.ILanguageInfo;

import javax.swing.text.Segment;

///////////////////////////////////////////////////////////////////
/**
 * Loro token marker.
 *
 * @author Carlos Rueda
 * @version 0.1 2001-01-29 (based on JavaTokenMarker by Slava Pestov)
 */
public class LoroTokenMarker extends CTokenMarker
{
	// private members
	private static KeywordMap keywords;

	///////////////////////////////////////////////////////////////////
	public LoroTokenMarker()
	{
		super(false,getKeywords());
	}

	///////////////////////////////////////////////////////////////////
	public LoroTokenMarker(
		String prompt,
		String prefix_special,
		String prefix_invalid
	)
	{
		super(false,getKeywords());
		this.prompt = prompt;
		if ( prompt != null ) {
			prompt_len = prompt.length();
		}
		this.prefix_special = prefix_special;
		if ( prefix_special != null ) {
			prefix_special_len = prefix_special.length();
		}
		this.prefix_invalid = prefix_invalid;
		if ( prefix_invalid != null ) {
			prefix_invalid_len = prefix_invalid.length();
		}
	}

	///////////////////////////////////////////////////////////////////
	public static KeywordMap getKeywords() {
		if(keywords == null) {
			keywords = new KeywordMap(false);
			ILanguageInfo langInfo = Loro.getLanguageInfo();
			String[] kws;
			kws = langInfo.getKeywords1();
			for ( int i = 0; i < kws.length; i++ ) {
				keywords.add(kws[i],Token.KEYWORD1);
			}
			kws = langInfo.getKeywords2();
			for ( int i = 0; i < kws.length; i++ ) {
				keywords.add(kws[i],Token.KEYWORD2);
			}
			kws = langInfo.getKeywords3();
			for ( int i = 0; i < kws.length; i++ ) {
				keywords.add(kws[i],Token.KEYWORD3);
			}
			kws = langInfo.getLiterals();
			for ( int i = 0; i < kws.length; i++ ) {
				keywords.add(kws[i],Token.LITERAL2);
			}
		}
		return keywords;
	}
}