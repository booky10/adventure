/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
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
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonElement;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleTest extends AbstractSerializeDeserializeTest<Style> {
  private static final Key FANCY_FONT = Key.of("kyori", "kittens");

  @Test
  void testWithDecorationAsColor() {
    final Style s0 = GsonComponentSerializer.GSON.fromJson(AbstractComponentTest.object(object -> {
      object.addProperty(StyleSerializer.COLOR, TextDecoration.NAMES.key(TextDecoration.BOLD));
    }), Style.class);
    assertNull(s0.color());
    assertTrue(s0.hasDecoration(TextDecoration.BOLD));
  }

  @Test
  void testWithResetAsColor() {
    final Style s0 = GsonComponentSerializer.GSON.fromJson(AbstractComponentTest.object(object -> {
      object.addProperty(StyleSerializer.COLOR, "reset");
    }), Style.class);
    assertNull(s0.color());
    assertThat(s0.decorations()).isEmpty();
  }

  @Override
  Stream<Map.Entry<Style, JsonElement>> tests() {
    final UUID dolores = UUID.randomUUID();
    return Stream.of(
      entry(Style.empty(), json -> {}),
      entry(Style.of(TextColor.of(0x0a1ab9)), json -> json.addProperty(StyleSerializer.COLOR, "#0a1ab9")),
      entry(Style.of(NamedTextColor.LIGHT_PURPLE), json -> json.addProperty(StyleSerializer.COLOR, name(NamedTextColor.LIGHT_PURPLE))),
      entry(Style.of(TextDecoration.BOLD), json -> json.addProperty(name(TextDecoration.BOLD), true)),
      entry(Style.builder().insertion("honk").build(), json -> json.addProperty(StyleSerializer.INSERTION, "honk")),
      entry(
        Style.builder()
          .font(FANCY_FONT)
          .color(NamedTextColor.RED)
          .decoration(TextDecoration.BOLD, true)
          .clickEvent(ClickEvent.openUrl("https://github.com"))
          .build(),
        json -> {
          json.addProperty(StyleSerializer.FONT, "kyori:kittens");
          json.addProperty(StyleSerializer.COLOR, name(NamedTextColor.RED));
          json.addProperty(name(TextDecoration.BOLD), true);
          json.add(StyleSerializer.CLICK_EVENT, object(clickEvent -> {
            clickEvent.addProperty(StyleSerializer.CLICK_EVENT_ACTION, name(ClickEvent.Action.OPEN_URL));
            clickEvent.addProperty(StyleSerializer.CLICK_EVENT_VALUE, "https://github.com");
          }));
        }
      ),
      entry(
        Style.builder()
          .hoverEvent(HoverEvent.showEntity(new HoverEvent.ShowEntity(
            Key.of(Key.MINECRAFT_NAMESPACE, "pig"),
            dolores,
            TextComponent.of("Dolores", TextColor.of(0x0a1ab9))
          )))
          .build(),
        json -> {
          json.add(StyleSerializer.HOVER_EVENT, object(hoverEvent -> {
            hoverEvent.addProperty(StyleSerializer.HOVER_EVENT_ACTION, name(HoverEvent.Action.SHOW_ENTITY));
            hoverEvent.add(StyleSerializer.HOVER_EVENT_CONTENTS, object(contents -> {
              contents.addProperty(ShowEntitySerializer.TYPE, "minecraft:pig");
              contents.addProperty(ShowEntitySerializer.ID, dolores.toString());
              contents.add(ShowEntitySerializer.NAME, object(name -> {
                name.addProperty(ComponentSerializerImpl.TEXT, "Dolores");
                name.addProperty(StyleSerializer.COLOR, "#0a1ab9");
              }));
            }));
          }));
        }
      ),
      showItem(1),
      showItem(2)
    );
  }

  private static Map.Entry<Style, JsonElement> showItem(final int count) {
    return entry(
      Style.builder()
        .hoverEvent(HoverEvent.showItem(new HoverEvent.ShowItem(
          Key.of(Key.MINECRAFT_NAMESPACE, "stone"),
          count
        )))
        .build(),
      json -> {
        json.add(StyleSerializer.HOVER_EVENT, object(hoverEvent -> {
          hoverEvent.addProperty(StyleSerializer.HOVER_EVENT_ACTION, name(HoverEvent.Action.SHOW_ITEM));
          hoverEvent.add(StyleSerializer.HOVER_EVENT_CONTENTS, object(contents -> {
            contents.addProperty(ShowItemSerializer.ID, "minecraft:stone");
            if(count != 1) {
              contents.addProperty(ShowItemSerializer.COUNT, count);
            }
          }));
        }));
      }
    );
  }

  static String name(final NamedTextColor color) {
    return NamedTextColor.NAMES.key(color);
  }

  static String name(final TextDecoration decoration) {
    return TextDecoration.NAMES.key(decoration);
  }

  static String name(final ClickEvent.Action action) {
    return ClickEvent.Action.NAMES.key(action);
  }

  static <V> String name(final HoverEvent.Action<V> action) {
    return HoverEvent.Action.NAMES.key(action);
  }

  @Override
  Style deserialize(final JsonElement json) {
    return GsonComponentSerializer.GSON.fromJson(json, Style.class);
  }

  @Override
  JsonElement serialize(final Style object) {
    return GsonComponentSerializer.GSON.toJsonTree(object);
  }
}
