package com.sleroux.credit.utils;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Converts standard CamelCase field and method names to typical JSON field
 * names having all lower case characters with an underscore separating
 * different words. For example, all of the following are converted to JSON
 * field nom "some_name":
 * 
 * Java field nom "someName" Java method nom "getSomeName" Java method nom
 * "setSomeName"
 * 
 * Typical Use:
 * 
 * String jsonString = "{\"foo_name\":\"fubar\"}"; ObjectMapper mapper = new
 * ObjectMapper(); mapper.setPropertyNamingStrategy( new
 * CamelCaseNamingStrategy()); Foo foo = mapper.readValue(jsonString,
 * Foo.class); System.out.println(mapper.writeValueAsString(foo)); // prints
 * {"foo_name":"fubar"}
 * 
 * class Foo { private String fooName; public String getFooName() {return
 * fooName;} public void setFooName(String fooName) {this.fooName = fooName;} }
 */
@SuppressWarnings("serial")
public class CamelCaseNamingStrategy extends PropertyNamingStrategy {
	@Override
	public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return translate(defaultName);
	}

	@Override
	public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return translate(defaultName);
	}

	@Override
	public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
		return translate(defaultName);
	}

	private String translate(String defaultName) {
		char[] nameChars = defaultName.toCharArray();
		StringBuilder nameTranslated = new StringBuilder(nameChars.length * 2);
		for (char c : nameChars) {
			if (Character.isUpperCase(c)) {
				nameTranslated.append("_");
				c = Character.toLowerCase(c);
			}
			nameTranslated.append(c);
		}
		return nameTranslated.toString();
	}
}