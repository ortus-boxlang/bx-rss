/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package ortus.boxlang.feed.util;

import ortus.boxlang.runtime.scopes.Key;

/**
 * This class is used to store all the keys used in the dictionary for this module.
 */
public class KeyDictionary {

	public static final Key	moduleName	= new Key( "bxrss" );

	// Feed component attributes
	public static final Key	action		= new Key( "action" );
	public static final Key	source		= new Key( "source" );
	public static final Key	result		= new Key( "result" );
	public static final Key	properties	= new Key( "properties" );
	public static final Key	data		= new Key( "data" );
	public static final Key	columnMap	= new Key( "columnMap" );
	public static final Key	outputFile	= new Key( "outputFile" );
	public static final Key	overwrite	= new Key( "overwrite" );
	public static final Key	xmlVar		= new Key( "xmlVar" );
	public static final Key	timeout		= new Key( "timeout" );
	public static final Key	userAgent	= new Key( "userAgent" );
	public static final Key	escapeChars	= new Key( "escapeChars" );

	// Deprecated CFML attributes (for backward compatibility)
	public static final Key	name		= new Key( "name" );
	public static final Key	query		= new Key( "query" );

}