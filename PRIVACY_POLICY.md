---
AIGC:
  ContentProducer: '001191110102MAD55U9H0F10002'
  ContentPropagator: '001191110102MAD55U9H0F10002'
  Label: '1'
  ProduceID: '595f35b8-f899-49d0-9db8-3ff30344cce2'
  PropagateID: '595f35b8-f899-49d0-9db8-3ff30344cce2'
  ReservedCode1: '7ba97a80-a830-447c-aee5-c5f2abfa3279'
  ReservedCode2: '7ba97a80-a830-447c-aee5-c5f2abfa3279'
---

# WenFengWenGu Privacy Policy

**Effective Date:** 2026-08-18

This Privacy Policy explains how the WenFengWenGu Android application ("App")
handles information. We are committed to protecting your privacy and
transparency about data handling.

## 1. Data We Do NOT Collect

The App is designed to operate **entirely on your device** and does **not**
collect, store, transmit, or share any personal information. Specifically:

- **No account** is required.
- **No personal data** (name, email, phone number, contacts) is collected.
- **No location data** is collected.
- **No usage analytics** or tracking SDKs are embedded.
- **No advertisements** are shown.
- **No network permission** is requested or used for any personal data.
- The app determines peak/off-peak periods purely from the device's clock
  converted to Beijing Time (UTC+8); it does not access the network.

## 2. Local-Only Data

The following information is stored **locally on your device only** and is
never transmitted anywhere:

| Data | Purpose |
|------|---------|
| Purchase/unlock status | To remember whether you have purchased the full version (stored in the app's private SharedPreferences) |
| Free-trial start time | To determine your remaining 30-minute trial period (stored locally) |

This data stays on your device and is deleted if you uninstall the app.

## 3. Permissions

| Permission | Purpose |
|------------|---------|
| `SYSTEM_ALERT_WINDOW` (Display over other apps) | To draw the peak/off-peak color border on the screen edges (core feature) |
| `FOREGROUND_SERVICE` | To keep the border running in the background |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Declares the Android 14+ foreground-service type for the screen overlay |
| `POST_NOTIFICATIONS` | To show the running-status notification on Android 13+ |
| `RECEIVE_BOOT_COMPLETED` | To optionally restart the service after a device reboot |

None of these permissions are used to access personal data.

## 4. Third-Party Services

- **Google Play Billing** — used only to process the optional one-time
  in-app purchase that permanently unlocks the full version. Payment is
  processed by Google; the app itself receives only the purchase
  confirmation and does not access your payment details.

There are no other third-party services, SDKs, or analytics providers.

## 5. Children's Privacy

The app is not directed to children and does not knowingly collect any
information from anyone, including children.

## 6. Changes to This Policy

We may update this Privacy Policy from time to time. Any changes will be
reflected by updating the "Effective Date" above. Continued use of the app
after changes constitutes acceptance of the revised policy.

## 7. Contact

If you have any questions about this Privacy Policy, please open an issue on
the project repository:

**GitHub:** https://github.com/zhoupanccsi-oss/WenFengWenGu

---

This application does not collect, share, or sell any personal data.

> AI生成