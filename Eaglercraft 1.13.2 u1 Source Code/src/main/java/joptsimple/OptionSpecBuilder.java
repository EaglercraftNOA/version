package joptsimple;

public class OptionSpecBuilder extends ArgumentAcceptingOptionSpec<Void> {

	OptionSpecBuilder(String name) {
		super(name, String.class, null);
	}
}
