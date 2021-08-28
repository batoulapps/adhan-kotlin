# Adhan Kotlin Multiplatform

[![badge-travis][]][travis] [![badge-cov][]][codecov]

Adhan is a well tested and well documented library for calculating Islamic prayer times. Adhan is written using Kotlin Multiplatform and works on multiple platforms. It has a small method overhead, and has no external dependencies.

All astronomical calculations are high precision equations directly from the book [“Astronomical Algorithms” by Jean Meeus](http://www.willbell.com/math/mc1.htm). This book is recommended by the Astronomical Applications Department of the U.S. Naval Observatory and the Earth System Research Laboratory of the National Oceanic and Atmospheric Administration.

Implementations of Adhan in other languages can be found in the parent repo [Adhan](https://github.com/batoulapps/Adhan).

This branch is for the Kotlin Multiplatform version. There is also a branch for a pure Java version of the library.

## Usage

### Gradle

```
implementation("com.batoulapps.adhan2:adhan:0.0.2")
```

**Note** - on Android, [kotlinx.datetime](https://github.com/Kotlin/kotlinx-datetime) uses `java.time`, which needs either a minimum api level of 26, or enabling of `coreLibraryDesugaring` as per the instructions [here](https://developer.android.com/studio/write/java8-support#library-desugaring).

### General Usage

To get prayer times, initialize a new `PrayerTimes` object passing in coordinates, date, and calculation parameters. The fields in this are `kotlinx.datetime.Instant` in UTC that can be converted to the wanted timezone.

```kotlin
val prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)
```

### Initialization parameters

#### Coordinates

Create a `Coordinates` object with the latitude and longitude for the location you want prayer times for.

```kotlin
val coordinates = Coordinates(35.78056, -78.6389);
```

#### Date

The date parameter passed in should be an instance of the `DateComponents` object. The year, month, and day values need to be populated. All other values will be ignored. The year, month and day values should be for the  local date that you want prayer times for. These date values are expected to be for the Gregorian calendar. There's also a convenience method for converting a `java.util.Date` to `DateComponents`.

```kotlin
val date = DateComponents(2015, 11, 1);
```

#### Calculation parameters

The rest of the needed information is contained within the `CalculationParameters` class. Instead of manually initializing this class, it is recommended to use one of the pre-populated instances in the `CalculationMethod` class. You can then further customize the calculation parameters if needed.

```kotlin
val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
  .copy(madhab = Madhab.HANAFI, prayerAdjustments = PrayerAdjustments(fajr = 2))
```

| Parameter | Description |
| --------- | ----------- |
| `method`    | CalculationMethod name |
| `fajrAngle` | Angle of the sun used to calculate Fajr |
| `ishaAngle` | Angle of the sun used to calculate Isha |
| `ishaInterval` | Minutes after Maghrib (if set, the time for Isha will be Maghrib plus ishaInterval) |
| `madhab` | Value from the Madhab object, used to calculate Asr |
| `highLatitudeRule` | Value from the HighLatitudeRule object, used to set a minimum time for Fajr and a max time for Isha |
| `adjustments` | JavaScript object with custom prayer time adjustments in minutes for each prayer time |

**CalculationMethod**

| Value | Description |
| ----- | ----------- |
| `MUSLIM_WORLD_LEAGUE` | Muslim World League. Fajr angle: 18, Isha angle: 17 |
| `EGYPTIAN` | Egyptian General Authority of Survey. Fajr angle: 19.5, Isha angle: 17.5 |
| `KARACHI` | University of Islamic Sciences, Karachi. Fajr angle: 18, Isha angle: 18 |
| `UMM_AL_QURA` | Umm al-Qura University, Makkah. Fajr angle: 18, Isha interval: 90. *Note: you should add a +30 minute custom adjustment for Isha during Ramadan.* |
| `DUBAI` | Method used in UAE. Fajr and Isha angles of 18.2 degrees. |
| `QATAR` | Modified version of Umm al-Qura used in Qatar. Fajr angle: 18, Isha interval: 90. |
| `KUWAIT` | Method used by the country of Kuwait. Fajr angle: 18, Isha angle: 17.5 |
| `MOONSIGHTING_COMMITTEE` | Moonsighting Committee. Fajr angle: 18, Isha angle: 18. Also uses seasonal adjustment values. |
| `SINGAPORE` | Method used by Singapore. Fajr angle: 20, Isha angle: 18. |
| `NORTH_AMERICA` | Referred to as the ISNA method. This method is included for completeness but is not recommended. Fajr angle: 15, Isha angle: 15 |
| `KUWAIT` | Kuwait. Fajr angle: 18, Isha angle: 17.5 |
| `OTHER` | Fajr angle: 0, Isha angle: 0. This is the default value for `method` when initializing a `CalculationParameters` object. |

**Madhab**

| Value | Description |
| ----- | ----------- |
| `SHAFI` | Earlier Asr time |
| `HANAFI` | Later Asr time |

**HighLatitudeRule**

| Value | Description |
| ----- | ----------- |
| `MIDDLE_OF_THE_NIGHT` | Fajr will never be earlier than the middle of the night and Isha will never be later than the middle of the night |
| `SEVENTH_OF_THE_NIGHT` | Fajr will never be earlier than the beginning of the last seventh of the night and Isha will never be later than the end of the first seventh of the night |
| `TWILIGHT_ANGLE` | Similar to `SEVENTH_OF_THE_NIGHT`, but instead of 1/7, the fraction of the night used is fajrAngle/60 and ishaAngle/60 |


### Prayer Times

Once the `PrayerTimes` object has been initialized it will contain values for all five prayer times and the time for sunrise. The prayer times will be  Date object instances initialized with UTC values. To display these times for the local timezone, a formatting and timezone conversion formatter should be used, for example `java.text.SimpleDateFormat`.

```kotlin
val formatter = SimpleDateFormat("hh:mm a")
formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"))
formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
```

### Qibla

As of version 1.1.0, this library provides a `Qibla` class for getting the qibla for a given location.

```kotlin
val coordinates = Coordinates(latitude, longitude);
val qibla = Qibla(coordinates);
// qibla.direction is the qibla direction
```

### SunnahTimes

The library provides a `SunnahTimes` class.

```kotlin
val sunnahTimes = SunnahTimes(prayerTimes);
// sunnahTimes.middleOfTheNight is the midpoint between Maghrib and Fajr
// sunnahTimes.lastThirdOfTheNight is the last third between Maghrib and Fajr
```

## Full Example

See an example in the `samples` module.

[badge-travis]: https://travis-ci.org/batoulapps/adhan-java.svg?branch=master
[badge-cov]: https://codecov.io/gh/batoulapps/adhan-java/branch/master/graph/badge.svg
[travis]: https://travis-ci.org/batoulapps/adhan-java
[codecov]: https://codecov.io/gh/batoulapps/adhan-java
