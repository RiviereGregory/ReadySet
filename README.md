# 🧳 ReadySet — Application de Listes de Bagages & Voyages

**ReadySet** (`fr.riverjach.readyset`) est une application Android moderne (MAD) conçue pour vous aider à préparer vos affaires de voyage ou de weekend sans stress ni oubli, utilisable **100% hors-ligne**.

---

## 🚀 Fonctionnalités du MVP (v1)

* **Templates de démarrage rapide** : Créez un voyage en 1 clic à partir de listes types (*Weekend Escapade*, *Vacances d'été*) ou partez d'une liste vierge.
* **Affichage à plat & Tri alphabétique** : Visualisez instantanément vos affaires ordonnées de A à Z.
* **Recherche instantanée** : Filtrez la liste en temps réel via la barre de recherche.
* **Suivi de progression** : Visualisez le pourcentage d'affaires emballées en temps réel.
* **Gestion locale & Offline-First** : Données persistées localement via Room Database sans compte requis.

---

## 🏛️ Architecture & Conception

Le projet suit les principes stricts de la **Clean Architecture** et du flux unidirectionnel **MVI / UDF** définis dans `.agents/rules/rules_android.md`.

```
ReadySet/
├── gradle/libs.versions.toml   # Centralisation Version Catalog
├── core/
│   ├── domain/                 # Kotlin pur (UseCases, Modèles, Ports SPI - 0 dépendance Android)
│   ├── data/                   # Implémentation Room, DAOs, Entités, Templates, Repositories
│   └── ui/                     # Design System Material 3, Thème et composants réutilisables
└── app/                        # Point d'entrée, Hilt DI, Navigation Compose, ViewModels & Écrans
```

### Découpage des Modules

| Module | Responsabilité | Dépendances |
| :--- | :--- | :--- |
| **`:core:domain`** | Entités (`Trip`, `PackingItem`), Objets-Valeurs (`TripId`, `ItemId`), UseCases purs. | Aucune (Kotlin JVM pur). |
| **`:core:data`** | Persistance Room (`ReadySetDatabase`), DAOs, Mappers DTO/DAO $\leftrightarrow$ Domaine. | `:core:domain`, Room, Coroutines. |
| **`:core:ui`** | Thème Material 3 (`ReadySetTheme`), barres de recherche et de progression. | `:core:domain`, Jetpack Compose BOM. |
| **`:app`** | Écrans Jetpack Compose (`TripListScreen`, `TripDetailScreen`), ViewModels MVI, Hilt DI. | `:core:domain`, `:core:data`, `:core:ui`, Hilt, Navigation. |

---

## 🧪 Stratégie de Test

* **Tests Unitaires Domaine (`:core:domain`)** : JUnit 5 + AssertJ sans mock via des Fake Repositories purs.
* **Tests Unitaires ViewModel (`:app`)** : Test des transitions d'états réactifs via **Turbine** et `StandardTestDispatcher`.
* **Tests IHM Compose sur JVM (`:app`)** : Tests des composants et interactions Compose sans émulateur via **Robolectric** (`TripDetailScreenTest`, `TripListScreenTest`).
* **Tests E2E Déclaratifs (Maestro)** : Automatisation des parcours utilisateurs complets (`.maestro/01_create_trip_and_pack.yaml`).

### Lancer les tests unitaires et IHM Compose (local JVM) :
```bash
./gradlew test
```

### Lancer les tests E2E avec Maestro (sur émulateur ou device) :
```bash
# Installation de Maestro : https://maestro.mobile.dev/
maestro test .maestro/01_create_trip_and_pack.yaml
```

---

## 🛠️ Compilation & Exécution

* **Configuration requise** : Android Studio Ladybug ou version ultérieure, JDK 17, Android SDK 35 (minSdk 26).
* **Compilation du projet** :
```bash
./gradlew assembleDebug
```
