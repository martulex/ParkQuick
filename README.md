ParkQuick – Mobile Parkplatzsuche & Parkzeitverwaltung 🚘

ParkQuick ist eine Android-Anwendung zur Verwaltung eigener Parkvorgänge und zum Auffinden von Community-Parkplätzen, entwickelt mit **Kotlin** und **Jetpack Compose**.

**Projektumfang & Status**

**Kernfunktionen (Vollständig implementiert / MVP)**

* Parkzeitverwaltung & Timer: Erstellung aktiver Parkvorgänge mit Restzeit-Countdown und Routing-Option.


* Intelligente Erinnerungen: System-Benachrichtigungen bei Parkzeitende sowie einstellbarer Vorwarnzeit (Lead Time).


* Discover-Bereich: Laden von Community-Spots aus Cloud Firestore.


* Historie: Übersicht für vergangene Parkvorgänge.


* App-Einstellungen: Konfiguration von Erinnerungszeiten und Dark Mode.

---

**Future Scope (Teils im Code/UI vorbereitet)**

Zugunsten eines stabilen Kern-Loops im MVP zurückgestellt (in Teilen jedoch im Code bzw. der UI bereits vorbereitet):

- Entkopplung von Parkplatz-Erstellung und Timer-Start.
- Such- und Filterleiste im Discover-Bereich.
- Start des Timers direkt aus dem DetailScreen.

**Zukünftige Erweiterungsideen (Reines Konzept)**

- Suche nach Parkplätzen direkt auf der Karte via Texteingabe (Google Places API).
- Nachträgliches Bearbeiten bereits erstellter Parkplatzeinträge.

---

**Tech-Stack**

* **Sprache:** Kotlin
* **UI-Toolkit:** Jetpack Compose (Material 3)
* **Architektur:** MVVM
* **Persistenz:** Room (SQLite), Preferences DataStore
* **Cloud:** Firebase Firestore & Storage
* **System-APIs:** Android Location Framework, AlarmManager, BroadcastReceiver, NotificationManager
