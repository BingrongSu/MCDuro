package net.robert.mcduro.key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.robert.mcduro.MCDuro;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinds {
    public static KeyBinding toggleSeeThoughKeyBinding;
    public static KeyBinding useSoulSkillKeyBinding;
    public static KeyBinding jueXingKeyBinding;
    public static KeyBinding checkWuHunKeyBinding;
    public static KeyBinding switchWuHunKeyBinding;
    public static KeyBinding increaseSkillIndexKeyBinding;
    public static KeyBinding decreaseSkillIndexKeyBinding;
    public static KeyBinding rightClickKeyBinding;

    public static void registerKeyBinds(){
        toggleSeeThoughKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.toggle_see_through", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));

        useSoulSkillKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.use_soul_skill", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));

        jueXingKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.jue_xing", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_J, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));

        checkWuHunKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.check_wuhun", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_K, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));

        switchWuHunKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.open_wuhun", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_TAB, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));

        increaseSkillIndexKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.increase_skill_index", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_UP, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));
        decreaseSkillIndexKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mcduro.decrease_skill_index", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_DOWN, // The keycode of the key
                "category.mcduro.mod_key_binds" // The translation key of the keybinding's category.
        ));

        MCDuro.LOGGER.info("Registering Key Binds");
    }
}
