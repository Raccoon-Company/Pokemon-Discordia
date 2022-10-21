package game.model;

public enum TypeAlteration {
    /**
     * A non-volatile status condition is a status condition that remains after being switched out.
     * It's displayed in the party screen, and the Pokémon's summary.
     * They can be cured by healing at a Pokémon Center, specific curative items, or other ways.
     * If a Pokémon is affected by a non-volatile status condition,
     * an icon will display the type of status condition (replacing the Pokémon's level in Generations I and II).
     *
     * A Pokémon cannot gain non-volatile status conditions when it is affected by Safeguard, Leaf Guard, Flower Veil, Shields Down, or Comatose.
     * A Pokémon will cure its status condition when affected by Refresh, Heal Bell, Aromatherapy, Psycho Shift, Jungle Healing, G-Max Sweetness, Natural Cure, Shed Skin, Hydration, or Lum Berry.
     *
     * a Pokémon cannot gain a non-volatile status condition if it's already afflicted by another one,
     * and a non-volatile status condition does not wear off automatically when the battle ends.
     * If a Pokémon under a status condition (such as a poisoned Cascoon) evolves,
     * the condition will be kept, even if the Pokémon gains a new type or Ability that would normally prevent it.
     */
    NON_VOLATILE,
    /**
     * A volatile status is a status condition that is inflicted by a move or Ability from another Pokémon
     * and will wear off when a Pokémon is switched out of battle or when a battle is over.
     * Many volatile status conditions will also wear off after a number of turns have passed.
     * A Pokémon can be affected by multiple volatile status conditions at a time.
     * A volatile status condition is not indicated by an icon.
     */
    VOLATILE,
    /**
     * A volatile battle status is usually self-inflicted and will wear off when a Pokémon is taken out of battle or a battle is over.
     * Many of these will also wear off after a number of turns pass.
     * Since they aren't shown in battle as a status condition (having an icon) a Pokémon can be affected with multiple volatile battle statuses,
     * volatile conditions and a non-volatile condition at the same time.
     */
    VOLATILE_BATTLE
}
