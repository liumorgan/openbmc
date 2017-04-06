# Copyright 2014-present Facebook. All Rights Reserved.
#
# This program file is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the
# Free Software Foundation; version 2 of the License.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program in a file named COPYING; if not, write to the
# Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor,
# Boston, MA 02110-1301 USA
SUMMARY = "Utilities"
DESCRIPTION = "Various utilities"
SECTION = "base"
PR = "r1"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "file://ast-functions \
           file://us_console.sh \
           file://sol-util \
           file://power_led.sh \
           file://power_util.py \
           file://post_led.sh \
           file://reset_usb.sh \
           file://setup-gpio.sh \
           file://setup_rov.sh \
           file://mdio.py \
           file://bcm5396.py \
           file://bcm5396_util.py \
           file://eth0_mac_fixup.sh \
           file://fby2_power.sh \
           file://power-on.sh \
           file://wedge_us_mac.sh \
           file://setup_switch.py \
           file://create_vlan_intf \
           file://watch-fc.sh \
           file://fcswitcher.sh \
           file://rc.early \
           file://rc.local \
           file://src \
           file://COPYING \
           file://check_slot_type.sh \
           file://setup-platform.sh \
          "

pkgdir = "utils"

S = "${WORKDIR}"

binfiles = "us_console.sh sol-util power_led.sh post_led.sh \
  reset_usb.sh mdio.py setup_rov.sh fby2_power.sh wedge_us_mac.sh \
  bcm5396.py bcm5396_util.py setup_switch.py watch-fc.sh power_util.py \
  check_slot_type.sh"

DEPENDS_append = "update-rc.d-native"

do_install() {
  dst="${D}/usr/local/fbpackages/${pkgdir}"
  install -d $dst
  install -m 644 ast-functions ${dst}/ast-functions
  localbindir="${D}/usr/local/bin"
  install -d ${localbindir}
  for f in ${binfiles}; do
      install -m 755 $f ${dst}/${f}
      ln -s ../fbpackages/${pkgdir}/${f} ${localbindir}/${f}
  done

  # common lib and include files
  install -d ${D}${includedir}/facebook
  install -m 0644 src/include/log.h ${D}${includedir}/facebook/log.h

  # init
  install -d ${D}${sysconfdir}/init.d
  install -d ${D}${sysconfdir}/rcS.d
  install -m 0755 ${WORKDIR}/rc.early ${D}${sysconfdir}/init.d/rc.early
  update-rc.d -r ${D} rc.early start 04 S .
  install -m 755 setup-gpio.sh ${D}${sysconfdir}/init.d/setup-gpio.sh
  update-rc.d -r ${D} setup-gpio.sh start 59 5 .
  # create VLAN intf automatically
  #install -d ${D}/${sysconfdir}/network/if-up.d
  #install -m 755 create_vlan_intf ${D}${sysconfdir}/network/if-up.d/create_vlan_intf
  # networking is done after rcS, any start level within rcS
  # for mac fixup should work
  #install -m 755 eth0_mac_fixup.sh ${D}${sysconfdir}/init.d/eth0_mac_fixup.sh
  #update-rc.d -r ${D} eth0_mac_fixup.sh start 70 S .
  install -m 755 power-on.sh ${D}${sysconfdir}/init.d/power-on.sh
  update-rc.d -r ${D} power-on.sh start 96 5 .
  #install -m 755 fcswitcher.sh ${D}${sysconfdir}/init.d/fcswitcher.sh
  #update-rc.d -r ${D} fcswitcher.sh start 90 S .
  install -m 0755 ${WORKDIR}/rc.local ${D}${sysconfdir}/init.d/rc.local
  update-rc.d -r ${D} rc.local start 99 2 3 4 5 .
  install -m 755 setup-platform.sh ${D}${sysconfdir}/init.d/setup-platform.sh
  update-rc.d -r ${D} setup-platform.sh start 91 S .
}

FILES_${PN} += "/usr/local ${sysconfdir}"
