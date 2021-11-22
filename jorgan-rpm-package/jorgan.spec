Name:           jorgan
Version:        4.0.0
Release:        1%{?dist}
Summary:        Java Virtual Organ
License:        GPL+
URL:            https://github.com/svenmeier/jorgan
Group:          Applications/Multimedia
BuildRequires:  ant java-11-openjdk alsa-lib-devel fluidsynth-devel desktop-file-utils
Requires:       fluidsynth java-11-openjdk
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

%description
Java Virtual Organ

%prep
rm *.rpm || true
rm -r x86_64 || true
sed -i 's/\/lib\//\/lib64\//' ../jorgan-package/src/debian/input/jorgan
sed -i 's/default-java/jre-openjdk/' ../build.properties

%build
ant -buildfile ../build.xml

%clean
rm -rf $RPM_BUILD_ROOT
sed -i 's/\/lib64\//\/lib\//' ../jorgan-package/src/debian/input/jorgan
sed -i 's/jre-openjdk/default-java/' ../build.properties

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p %{buildroot}%{_bindir}
mkdir -p %{buildroot}%{_libdir}/%{name}/lib
mkdir -p %{buildroot}%{_datadir}/icons/hicolor/16x16/apps
mkdir -p %{buildroot}%{_datadir}/icons/hicolor/16x16/mimetypes
mkdir -p %{buildroot}%{_datadir}/icons/hicolor/32x32/apps
mkdir -p %{buildroot}%{_datadir}/icons/hicolor/32x32/mimetypes
mkdir -p %{buildroot}%{_datadir}/icons/hicolor/48x48/apps
mkdir -p %{buildroot}%{_datadir}/icons/hicolor/48x48/mimetypes
mkdir -p %{buildroot}%{_datadir}/%{name}/skins
mkdir -p %{buildroot}%{_datadir}/%{name}/dispositions
mkdir -p %{buildroot}%{_datadir}/mime/packages
mkdir -p %{buildroot}%{_docdir}/%{name}
install -p -m 755 ../jorgan-package/src/debian/input/jorgan %{buildroot}%{_bindir}
install -p -m 644 ../jorgan-bootstrap/target/marshal/jorgan.jar %{buildroot}%{_libdir}/%{name}
install -p -m 644 ../jorgan-core/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-gui/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-creative/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-customizer/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-executor/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-fluidsynth/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-keyboard/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-importer/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-exporter/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-lan/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-lcd/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-linuxsampler/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-memory/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-midimerger/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-recorder/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-sams/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-soundfont/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-tools/target/marshal/lib/* %{buildroot}%{_libdir}/%{name}/lib
install -p -m 644 ../jorgan-package/src/debian/input/icons/hicolor/16x16/apps/jorgan.png %{buildroot}%{_datadir}/icons/hicolor/16x16/apps/jorgan.png
install -p -m 644 ../jorgan-package/src/debian/input/icons/hicolor/16x16/mimetypes/application-jorgan.disposition.png %{buildroot}%{_datadir}/icons/hicolor/16x16/mimetypes
install -p -m 644 ../jorgan-package/src/debian/input/icons/hicolor/32x32/apps/jorgan.png %{buildroot}%{_datadir}/icons/hicolor/32x32/apps
install -p -m 644 ../jorgan-package/src/debian/input/icons/hicolor/32x32/mimetypes/application-jorgan.disposition.png %{buildroot}%{_datadir}/icons/hicolor/32x32/mimetypes
install -p -m 644 ../jorgan-package/src/debian/input/icons/hicolor/48x48/apps/jorgan.png %{buildroot}%{_datadir}/icons/hicolor/48x48/apps
install -p -m 644 ../jorgan-package/src/debian/input/icons/hicolor/48x48/mimetypes/application-jorgan.disposition.png %{buildroot}%{_datadir}/icons/hicolor/48x48/mimetypes
install -p -m 644 ../jorgan-package/src/debian/jorgan.sharedmimeinfo %{buildroot}%{_datadir}/mime/packages/jorgan.xml
install -p -m 644 ../jorgan-package/src/debian/changelog %{buildroot}%{_docdir}/%{name}
install -p -m 644 ../jorgan-package/src/debian/input/copyright %{buildroot}%{_docdir}/%{name}
install -p -m 644 ../jorgan-core/docs/* %{buildroot}%{_docdir}/%{name}
install -p -m 644 ../jorgan-core/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-creative/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-executor/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-fluidsynth/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-lan/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-lcd/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-memory/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-recorder/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-sams/target/marshal/dispositions/* %{buildroot}%{_datadir}/%{name}/dispositions
install -p -m 644 ../jorgan-skins/target/marshal/skins/* %{buildroot}%{_datadir}/%{name}/skins
desktop-file-install --vendor="" --dir=%{buildroot}%{_datadir}/applications/ ../jorgan-package/src/debian/input/%{name}.desktop

%post
# automatically load snd-virmidi
if [ ! grep -q snd-virmidi /etc/modules ]; then
  printf "\n# jOrgan: automatically load snd-virmidi\nsnd-virmidi\n" >> /etc/modules;
  modprobe snd-virmidi;
fi

# keep virmidi from being loaded as first soundcard
if [ ! grep -q snd-virmidi /etc/modprobe.d/alsa-base.conf ]; then
  printf "\n# jOrgan: keep virmidi from being loaded as first soundcard\noptions snd-virmidi index=-2\n" >> /etc/modprobe.d/alsa-base.conf;
fi

# icons
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ]; then
  %{_bindir}/gtk-update-icon-cache -q %{_datadir}/icons/hicolor;
fi
update-mime-database %{_datadir}/mime &> /dev/null || :
update-desktop-database &> /dev/null || :

# Mime types
update-desktop-database

%postun
rm -rf /home/*/.jorgan/
rm -rf /home/*/.java/.userPrefs/jorgan/
rm -rf %{_libdir}/%{name}

# icons
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ]; then
  %{_bindir}/gtk-update-icon-cache -q %{_datadir}/icons/hicolor;
fi
update-mime-database %{_datadir}/mime &> /dev/null || :
update-desktop-database &> /dev/null || :

%files
%license ../jorgan-package/src/debian/input/copyright
%{_bindir}/%{name}
%{_libdir}/%{name}
%{_datadir}/%{name}
%{_docdir}/%{name}
%{_datadir}/applications/%{name}.desktop
%{_datadir}/icons/hicolor/*/apps/jorgan.png
%{_datadir}/icons/hicolor/*/mimetypes/application-jorgan.disposition.png
%{_datadir}/mime/packages/jorgan.xml
