package ortus.boxlang.feed.bifs;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.feed.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;

public class RSSTest extends BaseIntegrationTest {

	@DisplayName( "Test a basic rss bif feed" )
	@Test
	public void testBasicRSSFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
			feedItems = rss( 'https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml' );
			println( feedItems )
			count = feedItems.size()
			result = feedItems[ 1 ]
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 5 );
		// Verify that the result struct has the expected keys
		IStruct resultStruct = variables.getAsStruct( result );
		assertThat( resultStruct.containsKey( "title" ) ).isTrue();
		assertThat( resultStruct.containsKey( "content" ) ).isTrue();
		assertThat( resultStruct.containsKey( "description" ) ).isTrue();
		assertThat( resultStruct.containsKey( "pubdate" ) ).isTrue();
		assertThat( resultStruct.containsKey( "updated" ) ).isTrue();
		assertThat( resultStruct.containsKey( "link" ) ).isTrue();
		assertThat( resultStruct.containsKey( "author" ) ).isTrue();
		assertThat( resultStruct.containsKey( "category" ) ).isTrue();
		assertThat( resultStruct.containsKey( "categories" ) ).isTrue();
		assertThat( resultStruct.containsKey( "guid" ) ).isTrue();
		assertThat( resultStruct.containsKey( "isPermaLink" ) ).isTrue();
		assertThat( resultStruct.containsKey( "comments" ) ).isTrue();
		assertThat( resultStruct.containsKey( "enclosures" ) ).isTrue();
		assertThat( resultStruct.containsKey( "channelTitle" ) ).isTrue();
	}

	@DisplayName( "Test rss bif feed with filter closure" )
	@Test
	public void testRSSFeedWithFilter() {
		// @formatter:off
		runtime.executeSource(
		    """
			feedItems = rss(
				'https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml',
				( item ) -> false
			);
			println( feedItems )
			count = feedItems.size()
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isEqualTo( 0 );
	}

}
