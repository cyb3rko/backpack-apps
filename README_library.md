<p align="center">
  <img alt="BackPack" src="https://i.imgur.com/VW6M7N7.png" width="200"/>
</p>

<h1 align="center">BackPack - Handy, Modern, Secure</h1>

*[See here](README.md) for more general information about the Backpack apps.*

---

The goal of the Backpack library is deduplication of application code, easier maintance for multiple apps at one and prevention of silly mistakes when creating a new Backpack app.

---

Following components are included:

| **Component** | **Description** | **Implementation** |
|---|---|---|
| [BackpackApplication](backpack/src/main/kotlin/com/cyb3rko/backpack/BackpackApplication.kt) | Application class | Direct reference to from app's manifest |
| [BackpackMainActivity](backpack/src/main/kotlin/com/cyb3rko/backpack/activities/BackpackMainActivity.kt) | Base Settings activity | - super.onCreate<br>- binding = TheBinding.inflate(layoutInflater).asContentView()<br>- setSupportActionBar(binding.toolbar)<br>- findNavController(R.id.nav_host_id).applyToActionBar() |
| [BackpackSettingsActivity](backpack/src/main/kotlin/com/cyb3rko/backpack/activities/BackpackSettingsActivity.kt) | Base Settings Activity | - bindInterface<br>- super.onCreate<br>- BackpackSettings interface |
| [BackpackAnalysisFragment](backpack/src/main/kotlin/com/cyb3rko/backpack/fragments/BackpackAnalysisFragment.kt) | Base Analysis Fragment | - bindInterface(this)<br>- return super.onCreateView<br>- BackpackAnalysis interface |
| [BackpackMainFragment](backpack/src/main/kotlin/com/cyb3rko/backpack/fragments/BackpackMainFragment.kt) | Base Main Fragment | - own binding handling<br>- super.onCreateView (but return binding.root)<br>- bindInterface (onViewCreated)<br>- implement BackupFab<br>- BackpackMain interface |
| [BackpackSettingsFragment](backpack/src/main/kotlin/com/cyb3rko/backpack/fragments/BackpackSettingsFragment.kt) | Base Settings Fragment | - bindInterface<br>- super.onCreatePreferences<br>- BackpackSettingsView interface |
| [CryptoManager](backpack/src/main/kotlin/com/cyb3rko/backpack/crypto/CryptoManager.kt) | Almighty manager class for hashing, encryption / decryption, randoms | - see available methods |
| [BackupFab](backpack/src/main/kotlin/com/cyb3rko/backpack/views/BackupFab.kt) | Backup interface FAB | - <com.cyb3rko.backpack.views.BackupFab /> (match_parent, match_parent)<br>- set 4 event handlers setOnOpen, setOnClose, setOnImport, setOnExport |
| [AboutDialog](backpack/src/main/kotlin/com/cyb3rko/backpack/modals/AboutDialog.kt) | Dialog for Software and Device information | - call show(...), see parameter names |
| [ErrorDialog](backpack/src/main/kotlin/com/cyb3rko/backpack/modals/ErrorDialog.kt) | Dialog for all kinds of error | - call show(...) or showCustom(...) for custom error message, see parameter names |
| [ObjectSerializer](backpack/src/main/kotlin/com/cyb3rko/backpack/utils/ObjectSerializer.kt) | (De)Serializer for data objects | - call serialize(obj: Any): ByteArray or deserialize(bytes: ByteArray): Any |
| [Serializable](backpack/src/main/kotlin/com/cyb3rko/backpack/data/Serializable.kt) | Base data class for Backpack app content | - override 'const val serialVersionUID' to some random number<br>- [Random Long Generator](https://www.calculator.net/random-number-generator.html?slower=-9223372036854775808&supper=9223372036854775807&ctype=1) |
| [BuildInfo](backpack/src/main/kotlin/com/cyb3rko/backpack/data/BuildInfo.kt) | Data class to pass BuildConfig information to Backpack parent fragment instance | - see parameter names |
| [Themes](backpack/src/main/res) | Predefined themes for light and dark mode | - name="Theme.BackpackDemo" parent="Theme.Backpack" /><br>- name="TextAppearance.Toolbar.Subtitle" parent="Toolbar.Subtitle" /><br>- name="Preference.SwitchPreferenceCompat" parent="@style/PreferenceSwitch.Backpack" /><br>- v29: name="Theme.BackpackDemo" parent="Theme.Backpack.E2E" /> |
| [Data Extraction / Backup rules](backpack/src/main/res/xml) | Ready to use data extraction and backup rules for the app manifest | - android:dataExtractionRules="@xml/data_extraction_rules"<br>- android:fullBackupContent="@xml/backup_rules" |
| [Animations](backpack/src/main/res/anim) | Animations for fragment transitions | - shrink_in.xml / shrink_out.xml<br>- slide_in.xml / slide_out.xml |
| [Icons](backpack/src/main/res/drawable) | A few colored icons (credits already included) | - ic_arrow, ic_art, ic_empty, ic_git, ic_information, ic_information_scaled, ic_security, ic_settings |
