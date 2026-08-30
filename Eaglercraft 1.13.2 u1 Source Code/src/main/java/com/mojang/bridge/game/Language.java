package com.mojang.bridge.game;

/**
 * A representation of a language, used to display text.
 */
public interface Language {
    /**
     * A language code, such as "en_us".
     *
     * @return Language code
     */
    String getCode();

    /**
     * The name of the language.
     *
     * <p>This name is localized, so Swedish would be "Svenska"</p>
     *
     * @return Language name
     */
    String getName();

    /**
     * The region that the language is used in.
     *
     * <p>This name is localized, so Swedish would be "Sverige"</p>
     *
     * @return Language region
     */
    String getRegion();
}
