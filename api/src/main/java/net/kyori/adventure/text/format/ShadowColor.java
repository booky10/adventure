/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2024 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text.format;

import net.kyori.adventure.util.ARGBLike;
import net.kyori.adventure.util.RGBLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * A shadow color which may be applied to a {@link Style}.
 *
 * <p>Similar to {@link TextColor}, except that shadows contain alpha information.</p>
 *
 * @since 4.18.0
 * @sinceMinecraft 1.21.4
 */
public interface ShadowColor extends StyleBuilderApplicable, ARGBLike {
  /**
   * Return a shadow colour that will disable the shadow on a component.
   *
   * @return a disabling shadow color
   * @since 4.18.0
   */
  static @NotNull ShadowColor none() {
    return ShadowColorImpl.NONE;
  }

  /**
   * Create a new shadow color from the ARGB value packed in an int.
   *
   * @param value the int-packed ARGB value
   * @return a shadow color
   * @since 4.18.0
   */
  @Contract(pure = true)
  static @NotNull ShadowColor shadowColor(final @Range(from = 0x00, to = 0xffffffffL) int value) {
    if (value == ShadowColorImpl.NONE_VALUE) return none();

    return new ShadowColorImpl(value);
  }

  /**
   * Create a new shadow color from individual red, green, blue, and alpha values.
   *
   * @param red the red value
   * @param green the green value
   * @param blue the blue value
   * @param alpha the alpha
   * @return a shadow colour
   * @since 4.18.0
   */
  @Contract(pure = true)
  static @NotNull ShadowColor shadowColor(
    final @Range(from = 0x0, to = 0xff) int red,
    final @Range(from = 0x0, to = 0xff) int green,
    final @Range(from = 0x0, to = 0xff) int blue,
    final @Range(from = 0x0, to = 0xff) int alpha
  ) {
    final int value =
      (alpha & 0xff) << 24
        | (red & 0xff) << 16
        | (green & 0xff) << 8
        | (blue & 0xff);

    if (value == ShadowColorImpl.NONE_VALUE) return none();
    return new ShadowColorImpl(value);
  }

  /**
   * Create a shadow color from an existing colour plus an alpha value.
   *
   * @param color the existing color
   * @param alpha the alpha
   * @return a shadow colour
   * @since 4.18.0
   */
  @Contract(pure = true)
  static @NotNull ShadowColor shadowColor(final @NotNull RGBLike color, final @Range(from = 0x0, to = 0xff) int alpha) {
    return shadowColor(color.red(), color.green(), color.blue(), alpha);
  }

  /**
   * Create a shadow color from an existing ARGB colour.
   *
   * @param argb the existing color
   * @return a shadow colour
   * @since 4.18.0
   */
  static @NotNull ShadowColor shadowColor(final @NotNull ARGBLike argb) {
    if (argb instanceof ShadowColor) {
      return (ShadowColor) argb;
    }

    return shadowColor(argb.red(), argb.green(), argb.blue(), argb.alpha());
  }

  /**
   * Attempt to parse a shadow colour from a {@code #}-prefixed hex string.
   *
   * <p>This string should be in the format {@code #RRGGBBAA}</p>
   *
   * @param value the input value
   * @return a shadow color if possible, or null if any components are invalid
   * @since 4.18.0
   */
  @Contract(pure = true)
  static @Nullable ShadowColor fromHexString(@NotNull String value) {
    if (value.startsWith("#")) {
      value = value.substring(1);
    }
    if (value.length() != 8) {
      return null;
    }
    try {
      final int r = Integer.parseInt(value.substring(0, 2), 16);
      final int g = Integer.parseInt(value.substring(2, 4), 16);
      final int b = Integer.parseInt(value.substring(4, 6), 16);
      final int a = Integer.parseInt(value.substring(6, 8), 16);
      return new ShadowColorImpl((a << 24) | (r << 16) | (g << 8) | b);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  /**
   * Represent this shadow color as a {@code #}-prefixed hex string.
   *
   * <p>This string will be in the format {@code #RRGGBBAA}</p>
   *
   * @return the hex string representation of this shadow colour
   * @since 4.18.0
   */
  default @NotNull String asHexString() {
    final int argb = this.value();
    final int a = (argb >> 24) & 0xFF;
    final int r = (argb >> 16) & 0xFF;
    final int g = (argb >> 8) & 0xFF;
    final int b = argb & 0xFF;
    return String.format("#%02X%02X%02X%02X", r, g, b, a);
  }

  /**
   * Get the red component of the shadow colour.
   *
   * @return the red component, in the range [0x0, 0xff]
   * @since 4.18.0
   */
  @Override
  default @Range(from = 0x0, to = 0xff) int red() {
    return (this.value() >> 16) & 0xff;
  }

  /**
   * Get the green component of the shadow colour.
   *
   * @return the green component, in the range [0x0, 0xff]
   * @since 4.18.0
   */
  @Override
  default @Range(from = 0x0, to = 0xff) int green() {
    return (this.value() >> 8) & 0xff;
  }

  /**
   * Get the blue component of the shadow colour.
   *
   * @return the blue component, in the range [0x0, 0xff]
   * @since 4.18.0
   */
  @Override
  default @Range(from = 0x0, to = 0xff) int blue() {
    return this.value() & 0xff;
  }

  /**
   * Get the alpha component of the shadow colour.
   *
   * @return the blue component, in the range [0x0, 0xff]
   * @since 4.18.0
   */
  @Override
  default @Range(from = 0x0, to = 0xff) int alpha() {
    return (this.value() >> 24) & 0xff;
  }

  /**
   * The int-packed ARGB value of this shadow colour.
   *
   * @return the shadow colour value
   * @since 4.18.0
   */
  int value();

  @Override
  default void styleApply(final Style.@NotNull Builder style) {
    style.shadowColor(this);
  }
}
