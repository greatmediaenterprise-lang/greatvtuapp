# Implementation Plan - Task_3_VTU_Bills_History_Adaptive

Implement VTU services, Utility payments, and an adaptive Transaction History log using Navigation 3 and Compose Material Adaptive.

## User Review Required

> [!IMPORTANT]
> This implementation assumes the existence of `transactions` table in Supabase with fields: `id`, `user_id`, `type`, `service_name`, `amount`, `status`, `created_at`, and `details`.

## Proposed Changes

### Data Layer

#### [NEW] [Transaction.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/data/model/Transaction.kt)
Define the `Transaction` data class to represent VTU and Bill payment records.

#### [NEW] [TransactionRepository.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/data/repository/TransactionRepository.kt)
Repository to handle fetching transaction history and performing VTU/Utility payments (simulated or real Supabase calls).

### ViewModels

#### [NEW] [TransactionViewModel.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/viewmodel/TransactionViewModel.kt)
ViewModel to manage transaction list, search state, and selected transaction for the detail pane.

#### [NEW] [VTUViewModel.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/viewmodel/VTUViewModel.kt)
ViewModel to handle VTU (Airtime/Data) and Utility bill payment logic.

### UI Layer - Navigation

#### [MODIFY] [NavKey.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/ui/navigation/NavKey.kt)
Add new routes for `VTU`, `Utilities`, and `Receipt`.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/MainActivity.kt)
Register new `NavKey` entries in the `NavDisplay`.

### UI Layer - Screens

#### [NEW] [VTUScreen.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/ui/screen/VTUScreen.kt)
Multi-Network VTU services (Airtime/Data) and Utility payment forms.

#### [NEW] [TransactionsScreen.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/ui/screen/TransactionsScreen.kt)
Searchable transaction log using `ListDetailPaneScaffold`.

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/DELL/AndroidStudioProjects/GREATVTU/app/src/main/java/com/example/greatvtu/ui/screen/DashboardScreen.kt)
Update the Dashboard to include entry points for VTU and Utilities.

## Verification Plan

### Automated Tests
- Build the project: `./gradlew :app:assembleDebug`

### Manual Verification
- Verify that clicking VTU/Utilities opens the respective forms.
- Verify transaction history displays items in a list.
- Verify clicking a transaction opens the detail pane (Receipt).
- Verify search functionality in transaction history.
- Verify adaptive behavior on different screen sizes (using Previews).
