/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.stringFormat.StringFormat

interface WithGuideBreaks<DomainT> {
    val breaks: List<DomainT>
    val formatter: StringFormat
}
