# How to use the Dynamic Form Builder

This guide walks through the app from a user's perspective — creating workspaces, building forms, collecting responses, and retrieving uploaded files.

---

## Contents

- [Overview](#overview)
- [Workspaces](#workspaces)
- [Forms](#forms)
- [Form Builder](#form-builder)
  - [Field types](#field-types)
  - [Adding options to a field](#adding-options-to-a-field)
- [Collecting responses](#collecting-responses)
  - [File uploads](#file-uploads)
- [Viewing responses](#viewing-responses)

---

## Overview

The app is organised into three levels:

```
Workspaces  →  Forms  →  Fields / Responses
```

A **workspace** groups related forms together (e.g. by team, project, or data-collection campaign). Each **form** is an independent set of **fields** that respondents fill in. Responses are stored per-form and can include file attachments.

---

## Workspaces

When you open the app you land on the **Workspaces** page.

**Creating a workspace**

1. Click **+ New Workspace**.
2. Enter a display name (e.g. *Anti-Corruption Unit*).
3. Enter a URL slug — a short, lowercase identifier with no spaces (e.g. *anti-corruption*). This is used internally and must be unique.
4. Click **Create Workspace**.

The workspace appears in the list immediately.

**Opening a workspace**

Click **Open →** on any workspace card to enter it and see its forms.

**Deleting a workspace**

Click **Delete** on a workspace card. The workspace is soft-deleted and removed from the list.

---

## Forms

Inside a workspace you see all forms that belong to it.

**Creating a form**

1. Click **+ New Form**.
2. Enter a title (e.g. *Beneficial Ownership Declaration*) and an optional description.
3. Click **Create Form**.

**Navigating a form**

Each form card has two action buttons:

| Button | Takes you to |
|---|---|
| **Builder** | The form builder, where you define the fields |
| **Submissions** | The submissions page, where you fill in and view responses |

**Deleting a form**

Click **Delete** on a form card. The form and all its fields are soft-deleted.

---

## Form Builder

The builder is where you define what a form asks. Open it by clicking **Builder** on a form card.

**Adding a field**

1. Click **+ Add Field** (top right).
2. Fill in the panel that appears:
   - **Label** — the question text shown to respondents.
   - **Type** — the kind of input (see [Field types](#field-types) below).
   - **Required** — tick this to make the field mandatory on submission.
3. If the type is *Dropdown*, *Checkbox*, or *Radio*, an **Options** textarea appears — enter one option per line.
4. Click **Add Field**. The field is saved immediately.

**Removing a field**

Click **Remove** on any field row. The field is deleted immediately.

**Navigating away**

Two buttons appear at the bottom of the builder page:

| Button | Action |
|---|---|
| **← back to Forms** | Returns to the forms list for this workspace |
| **to Submissions →** | Goes to the submissions page for this form |

---

### Field types

| Type | Input rendered for respondents |
|---|---|
| **Text** | Single-line text box |
| **Text area** | Multi-line text box |
| **Number** | Numeric input |
| **Date** | Date picker |
| **Dropdown** | Select menu with the options you defined |
| **Checkbox** | One or more tick-boxes (one option = yes/no; multiple options = multi-select) |
| **Radio** | Single-choice radio buttons from the options you defined |
| **File** | File chooser — the uploaded file is stored in MinIO |

---

### Adding options to a field

For **Dropdown**, **Checkbox**, and **Radio** fields, an **Options** box appears in the add-field panel when you select those types.

Enter each option on its own line:

```
United Kingdom
United States
France
Germany
```

The builder shows how many options have been saved (e.g. *4 options*) in the field list.

---

## Collecting responses

Go to **Submissions** (either from the form card or the builder's *to Submissions →* button). The top section of the page shows the submission form with all the fields you built.

**Filling in a response**

1. Fill in each field. Required fields are marked with a red asterisk (**\***).
2. Click **Submit response**.

If a required field is left empty, a warning banner appears and submission is blocked until the field is filled in.

After a successful submission the form resets and the new response appears in the **Responses** list below.

---

### File uploads

When a form contains a **File** field:

1. Click the file chooser and select a file from your device.
2. Submit the form as normal.

The file is uploaded to the object store before the response is saved. Progress is synchronous — the Submit button completes only after the upload finishes.

> **Size limit:** files up to 100 MB are accepted.

---

## Viewing responses

All responses appear in the **Responses** section of the submissions page, newest first.

**Expanding a response**

Click a response row to expand it. Each field label and its submitted value are shown side by side.

**Downloading a file**

For fields that received a file upload, the value column shows a download icon (↓) instead of text. Click it to download the file directly from the object store.

**Response count**

The *Responses* heading shows a live count that increments with each new submission.

**Navigating back**

Click **← back to Forms** (top right of the submissions page) to return to the forms list.