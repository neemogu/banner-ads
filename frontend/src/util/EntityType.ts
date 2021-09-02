export type EntityType = "banner"|"category";

export const entityPluralForm = new Map<EntityType, string>([
        ["banner", "banners"],
        ["category", "categories"]
    ]);
