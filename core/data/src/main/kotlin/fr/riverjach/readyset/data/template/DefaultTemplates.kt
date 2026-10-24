package fr.riverjach.readyset.data.template

import fr.riverjach.readyset.domain.model.ItemCategory
import fr.riverjach.readyset.domain.model.PackingTemplate
import fr.riverjach.readyset.domain.model.TemplateItem

object DefaultTemplates {

    val weekendTemplate = PackingTemplate(
        id = "tpl-weekend",
        title = "Weekend Escapade (2-3 jours)",
        description = "Les essentiels indispensables pour partir léger sans rien oublier.",
        items = listOf(
            TemplateItem("Sous-vêtements (x3)", ItemCategory.CLOTHES, 3),
            TemplateItem("Chaussettes (x3)", ItemCategory.CLOTHES, 3),
            TemplateItem("T-shirts (x2)", ItemCategory.CLOTHES, 2),
            TemplateItem("Pantalon / Jean de rechange", ItemCategory.CLOTHES, 1),
            TemplateItem("Pyjama", ItemCategory.CLOTHES, 1),
            TemplateItem("Brosse à dents & Dentifrice", ItemCategory.TOILETRIES, 1),
            TemplateItem("Gel douche / Shampooing format voyage", ItemCategory.TOILETRIES, 1),
            TemplateItem("Déodorant", ItemCategory.TOILETRIES, 1),
            TemplateItem("Carte d'identité / Passeport", ItemCategory.DOCUMENTS, 1),
            TemplateItem("Billets de train / avion ou réservations", ItemCategory.DOCUMENTS, 1),
            TemplateItem("Carte bancaire & Espèces", ItemCategory.DOCUMENTS, 1),
            TemplateItem("Chargeur de téléphone", ItemCategory.TECH, 1),
            TemplateItem("Batterie externe", ItemCategory.TECH, 1),
            TemplateItem("Écouteurs", ItemCategory.TECH, 1),
            TemplateItem("Lunettes de soleil", ItemCategory.MISC, 1),
            TemplateItem("Mouchoirs", ItemCategory.MISC, 1),
            TemplateItem("Petite trousse à pharmacie (Doliprane, pansements)", ItemCategory.MISC, 1)
        )
    )

    val summerVacationTemplate = PackingTemplate(
        id = "tpl-summer",
        title = "Vacances d'Été (1 semaine)",
        description = "Tout le nécessaire pour profiter du soleil et de la mer.",
        items = listOf(
            TemplateItem("Sous-vêtements (x7)", ItemCategory.CLOTHES, 7),
            TemplateItem("Chaussettes (x7)", ItemCategory.CLOTHES, 7),
            TemplateItem("T-shirts / Débardeurs (x6)", ItemCategory.CLOTHES, 6),
            TemplateItem("Shorts / Bermudas (x3)", ItemCategory.CLOTHES, 3),
            TemplateItem("Maillots de bain (x2)", ItemCategory.CLOTHES, 2),
            TemplateItem("Serviette de plage", ItemCategory.MISC, 1),
            TemplateItem("Crème solaire & Biafine", ItemCategory.TOILETRIES, 1),
            TemplateItem("Brosse à dents & Dentifrice", ItemCategory.TOILETRIES, 1),
            TemplateItem("Gel douche & Shampooing", ItemCategory.TOILETRIES, 1),
            TemplateItem("Carte d'identité / Passeport", ItemCategory.DOCUMENTS, 1),
            TemplateItem("Permis de conduire", ItemCategory.DOCUMENTS, 1),
            TemplateItem("Carte Vitale / Assurance", ItemCategory.DOCUMENTS, 1),
            TemplateItem("Chargeurs & Adaptateur secteur", ItemCategory.TECH, 1),
            TemplateItem("Enceinte Bluetooth", ItemCategory.TECH, 1),
            TemplateItem("Liseuse ou Livre", ItemCategory.MISC, 1),
            TemplateItem("Casquette ou Chapeau", ItemCategory.CLOTHES, 1),
            TemplateItem("Lunettes de soleil", ItemCategory.MISC, 1)
        )
    )

    val emptyTemplate = PackingTemplate(
        id = "tpl-empty",
        title = "Liste Vierge",
        description = "Commencez avec une valise vide et ajoutez vos propres affaires.",
        items = emptyList()
    )

    val allTemplates: List<PackingTemplate> = listOf(
        weekendTemplate,
        summerVacationTemplate,
        emptyTemplate
    )
}
