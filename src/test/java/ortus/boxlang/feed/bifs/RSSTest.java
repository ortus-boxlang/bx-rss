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
			feedData = rss( 'https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml' );
			println( feedData )
			feedItems = feedData.items
			channel = feedData.channel
			count = feedItems.size()
			result = feedItems[ 1 ]
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 5 );

		// Verify channel metadata
		IStruct channelStruct = variables.getAsStruct( Key.of( "channel" ) );
		assertThat( channelStruct.containsKey( "title" ) ).isTrue();
		assertThat( channelStruct.containsKey( "description" ) ).isTrue();
		assertThat( channelStruct.containsKey( "link" ) ).isTrue();
		assertThat( channelStruct.containsKey( "language" ) ).isTrue();

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
			feedData = rss(
				'https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml',
				( item ) -> false
			);
			println( feedData )
			count = feedData.items.size()
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isEqualTo( 0 );
	}

	@DisplayName( "Test rss bif feed with maxItems limit" )
	@Test
	public void testRSSFeedWithMaxItems() {
		// @formatter:off
		runtime.executeSource(
		    """
			feedData = rss( urls='https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml', maxItems=3 );
			println( feedData )
			count = feedData.items.size()
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isEqualTo( 3 );
	}

	@DisplayName( "Test rss bif with iTunes podcast feed" )
	@Test
	public void testItunesPodcastFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
			feedData = rss( urls='https://feeds.theincomparable.com/batmanuniversity', itunes=true );
			println( feedData )
			feedItems = feedData.items
			channel = feedData.channel
			count = feedItems.size()
			result = feedItems[ 1 ]
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 1 );

		// Verify iTunes channel metadata
		IStruct channelStruct = variables.getAsStruct( Key.of( "channel" ) );
		assertThat( channelStruct.containsKey( "itunesImage" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesCategories" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesExplicit" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesAuthor" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesTitle" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesSubtitle" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesSummary" ) ).isTrue();
		assertThat( channelStruct.containsKey( "itunesOwner" ) ).isTrue();

		// Verify iTunes item fields
		IStruct resultStruct = variables.getAsStruct( result );
		assertThat( resultStruct.containsKey( "itunesDuration" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesExplicit" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesTitle" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesSubtitle" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesSummary" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesKeywords" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesEpisode" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesSeason" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesEpisodeType" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesImage" ) ).isTrue();
		assertThat( resultStruct.containsKey( "itunesBlock" ) ).isTrue();
	}

	@DisplayName( "Test rss bif with Media RSS feed" )
	@Test
	public void testMediaRssFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
			feedData = rss( urls='https://vimeo.com/channels/staffpicks/videos/rss', mediaRss=true );
			println( feedData )
			feedItems = feedData.items
			count = feedItems.size()
			result = feedItems[ 1 ]
			""",
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 1 );

		// Verify Media RSS item fields
		IStruct resultStruct = variables.getAsStruct( result );
		assertThat( resultStruct.containsKey( "mediaThumbnail" ) ).isTrue();

		// Verify thumbnail structure
		IStruct thumbnailStruct = resultStruct.getAsStruct( Key.of( "mediaThumbnail" ) );
		assertThat( thumbnailStruct.containsKey( "url" ) ).isTrue();
		assertThat( thumbnailStruct.containsKey( "width" ) ).isTrue();
		assertThat( thumbnailStruct.containsKey( "height" ) ).isTrue();
		assertThat( thumbnailStruct.containsKey( "time" ) ).isTrue();
	}

}
