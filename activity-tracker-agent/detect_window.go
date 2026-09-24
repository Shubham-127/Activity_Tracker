package main

import (
	"syscall"
	"unsafe"
	"time"
)

var (
	user32                  = syscall.NewLazyDLL("user32.dll")
kernel32 = syscall.NewLazyDLL("kernel32.dll")
	procGetForegroundWindow = user32.NewProc("GetForegroundWindow")
	procGetWindowTextW      = user32.NewProc("GetWindowTextW")
	
)



func getActiveWindowTitle() string {
	hwnd, _, _ := procGetForegroundWindow.Call()

	buf := make([]uint16, 256)
	procGetWindowTextW.Call(
		hwnd,
		uintptr(unsafe.Pointer(&buf[0])),
		uintptr(len(buf)),
	)
	return syscall.UTF16ToString(buf)
}

type lastInputInfo struct{
	cbSize uint32
	dwTime uint32
}
	
var procGetLastInputInfo = user32.NewProc("GetLastInputInfo")
var proGetTickCount = kernel32.NewProc("GetTickCount")

func getIdleDuration() time.Duration{
	var lii lastInputInfo
	lii.cbSize = uint32(unsafe.Sizeof(lii))

	procGetLastInputInfo.Call(uintptr(unsafe.Pointer(&lii)))

	tickCount,_,_:=proGetTickCount.Call()
	idleMillis := uint32(tickCount) - lii.dwTime
	return time.Duration(idleMillis) * time.Millisecond
}
