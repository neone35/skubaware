# SkubaWare

## Overview

Multi list warehouse inventory manager with filtering options. Allows to move products
between two lists, sort & filter first list and export the result.

## Description

This is a technical task provided by [LLC "Skuba"](https://www.skuba.lt/lithuania/) at 2023.10.25
for Android
Developer position.

**Requirements**

1. Create main view with two lists.
2. Upload given json data into the first list. Display image if url is available.
3. Tapping each data item should transfer it between two lists.
4. Provide an option to filter & sort shown data.

**Additional implementations**

- When there is enough width (600dp+) two lists are shown side by side.
- Dark / light mode switch
- Final list export into .txt file and opening with supported external app.

**Design**

As there was no given design mockup or even wireframe,
I've created approximate
[figma design](https://www.figma.com/file/rZp3lEMDayeuj4fyotgvKK/skubaware?type=design&node-id=0%3A1&mode=design&t=ykZSJQNMrXNppetS-1)
to better understand app behavior. As app is not too complex, design was also used as a guide to
app workflow instead of creating a diagram in draw.io.

## Screenshots

![SkubaWare screenshots](https://i.ibb.co/V95g1SM/skubaware-screens.png "SkubaWare screenshots")

## What Did I Use?

- MVVM architecture pattern
- Kotlin Coroutines
- Koin DI
- Jetpack Compose
- Material design components

## TO DO

*This is still project in progress but it already satisfies requirements from 2023.10.28.*

- rewrite API error handling
- put names and versions of gradle dependencies into objects