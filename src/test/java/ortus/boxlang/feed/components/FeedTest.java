package ortus.boxlang.feed.components;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.feed.BaseIntegrationTest;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;

public class FeedTest extends BaseIntegrationTest {

	@Test
	@DisplayName( "Can read RSS feed using Feed component" )
	public void testReadRSSFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" result="feedData" maxItems="5";
		    count = feedData.items.size();
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 1 );

		IStruct feedData = variables.getAsStruct( Key.of( "feedData" ) );
		assertThat( feedData ).isNotNull();
		assertThat( feedData.containsKey( "items" ) ).isTrue();
		assertThat( feedData.containsKey( "channel" ) ).isTrue();
	}

	@Test
	@DisplayName( "Can read Atom feed using Feed component" )
	public void testReadAtomFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://feeds.feedburner.com/TheHackersNews" result="feedData" maxItems="3";
		    count = feedData.items.size();
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 1 );
	}

	@Test
	@DisplayName( "Can create RSS 2.0 feed with array data" )
	public void testCreateRSSFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Test Feed",
		        description: "A test RSS feed",
		        link: "https://example.com",
		        author: "Test Author",
		        language: "en-US"
		    };

		    data = [
		        {
		            title: "First Post",
		            link: "https://example.com/post1",
		            description: "This is the first post",
		            author: "John Doe",
		            publishedDate: now()
		        },
		        {
		            title: "Second Post",
		            link: "https://example.com/post2",
		            description: "This is the second post",
		            author: "Jane Smith",
		            publishedDate: now()
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" xmlVar="feedXml";

		    hasXml = len(feedXml) > 0;
		    containsRss = findNoCase("<rss", feedXml) > 0;
		    containsTitle = findNoCase("Test Feed", feedXml) > 0;
		    containsFirstPost = findNoCase("First Post", feedXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasXml" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsRss" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsTitle" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsFirstPost" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create Atom 1.0 feed" )
	public void testCreateAtomFeed() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Test Atom Feed",
		        description: "A test Atom feed",
		        link: "https://example.com/atom"
		    };

		    data = [
		        {
		            title: "Atom Entry 1",
		            link: "https://example.com/entry1",
		            content: "Content of first entry"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" feedType="atom_1.0" xmlVar="atomXml";

		    hasXml = len(atomXml) > 0;
		    containsAtom = findNoCase("<feed", atomXml) > 0;
		    containsTitle = findNoCase("Test Atom Feed", atomXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasXml" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsAtom" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsTitle" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can write feed to output file" )
	public void testWriteFeedToFile() throws IOException {
		File tempFile = File.createTempFile( "test-feed-", ".xml" );
		tempFile.deleteOnExit();
		String filePath = tempFile.getAbsolutePath();

		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "File Test Feed",
		        description: "Testing file output",
		        link: "https://example.com"
		    };

		    data = [
		        {
		            title: "Test Item",
		            link: "https://example.com/item",
		            description: "Test description"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" outputFile="@filePath@" overwrite="true";

		    fileExists = fileExists("@filePath@");
		    """.replace( "@filePath@", filePath ),
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "fileExists" ) ) ).isTrue();

		// Verify file contents
		String content = Files.readString( tempFile.toPath() );
		assertThat( content ).contains( "File Test Feed" );
		assertThat( content ).contains( "Test Item" );
	}

	@Test
	@DisplayName( "Can use columnMap for query data" )
	public void testColumnMap() {
		// @formatter:off
		runtime.executeSource(
		    """
		    // Simulate query with different column names
		    data = [
		        {
		            headline: "News Item 1",
		            url: "https://example.com/news1",
		            body: "News body content",
		            writer: "Reporter Name"
		        },
		        {
		            headline: "News Item 2",
		            url: "https://example.com/news2",
		            body: "More news content",
		            writer: "Another Reporter"
		        }
		    ];

		    columnMap = {
		        title: "headline",
		        link: "url",
		        description: "body",
		        author: "writer"
		    };

		    properties = {
		        title: "News Feed",
		        description: "Latest news",
		        link: "https://example.com/news"
		    };

		    bx:feed action="create" properties="#properties#" data="#data#" columnMap="#columnMap#" xmlVar="newsXml";

		    containsHeadline = findNoCase("News Item 1", newsXml) > 0;
		    containsUrl = findNoCase("https://example.com/news1", newsXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "containsHeadline" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "containsUrl" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can escape special characters with escapeChars" )
	public void testEscapeChars() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Test & Special <Characters>",
		        description: "Testing escaping",
		        link: "https://example.com"
		    };

		    data = [
		        {
		            title: "Post with <tags> & ampersands",
		            link: "https://example.com/post",
		            description: "Content with 'quotes' and <html>"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" escapeChars="true" xmlVar="escapedXml";

		    // Escaped version should contain &lt; instead of <
		    containsEscaped = findNoCase("&lt;", escapedXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "containsEscaped" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Read action fails without source attribute" )
	public void testReadWithoutSource() {
		assertThrows( Exception.class, () -> {
			runtime.executeSource(
			    """
			    bx:feed action="read" result="feedData";
			    """,
			    context
			);
		} );
	}

	@Test
	@DisplayName( "Create action fails without properties attribute" )
	public void testCreateWithoutProperties() {
		assertThrows( Exception.class, () -> {
			runtime.executeSource(
			    """
			    data = [ { title: "Test" } ];
			    bx:feed action="create" data="#data#" xmlVar="xml";
			    """,
			    context
			);
		} );
	}

	@Test
	@DisplayName( "Create action fails without data attribute" )
	public void testCreateWithoutData() {
		assertThrows( Exception.class, () -> {
			runtime.executeSource(
			    """
			    properties = { title: "Test", link: "https://example.com" };
			    bx:feed action="create" properties="#properties#" xmlVar="xml";
			    """,
			    context
			);
		} );
	}

	@Test
	@DisplayName( "Supports backward compatibility with 'name' attribute" )
	public void testBackwardCompatibilityName() {
		// @formatter:off
		runtime.executeSource(
		    """
		    bx:feed action="read" source="https://www.engadget.com/rss.xml" name="oldStyleResult" maxItems="2";
		    hasResult = isDefined("oldStyleResult");
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasResult" ) ) ).isTrue();
	}

	@Test
	@DisplayName( "Can create feed with categories" )
	public void testFeedWithCategories() {
		// @formatter:off
		runtime.executeSource(
		    """
		    properties = {
		        title: "Category Test Feed",
		        description: "Testing categories",
		        link: "https://example.com"
		    };

		    data = [
		        {
		            title: "Categorized Post",
		            link: "https://example.com/cat-post",
		            description: "Post with categories",
		            category: "Tech,News,Development"
		        }
		    ];

		    bx:feed action="create" properties="#properties#" data="#data#" xmlVar="catXml";

		    hasTechCategory = findNoCase("Tech", catXml) > 0;
		    hasNewsCategory = findNoCase("News", catXml) > 0;
		    """,
		    context
		);
		// @formatter:on

		assertThat( variables.getAsBoolean( Key.of( "hasTechCategory" ) ) ).isTrue();
		assertThat( variables.getAsBoolean( Key.of( "hasNewsCategory" ) ) ).isTrue();
	}

}
